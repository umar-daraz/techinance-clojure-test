(ns pingpong.core
  (:gen-class)
  (:require [io.pedestal.http :as http]
            [io.pedestal.service-tools.dev :as service-tools]
            [io.pedestal.http.route :as route]
            [ring.util.http-response :as response]
            [ring.middleware.resource :as resource]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [db :as db]
            ))

(defn resources
  "Retrieve a resource from /resources. `lein watch-cljs` will dump js resources
  in /resources/public/js/."
  [request]
  (resource/resource-request request ""))

(defn index
  [_request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (slurp (io/resource "public/index.html"))})

(def first-user-id 1)
(defonce user-cursor (atom first-user-id))

(defn ping-response [{:keys [ping user]}]
  (let [user-name (if (nil? user) "" (:first-name user))]
    (if (string/blank? ping)
      {:ping "pong" :name user-name}
      {:ping ping :name user-name})))


(defn pong
  "Implementation of the `/api/ping` endpoint."
  [{{:keys [value]} :params}]
  (let [user (db/fetch-user  @user-cursor)
        next-user-id (if (nil? user) (inc first-user-id) (inc @user-cursor))
        user (if (nil? user) (db/fetch-user first-user-id) user)]
    (reset! user-cursor  next-user-id)
    (response/ok
     (ping-response {:ping value :user user }))))

(def routes
  (route/expand-routes
   #{["/" :get index :route-name :index]
     ["/api/ping" :get [http/json-body pong] :route-name :pong]
     ["/public/*" :get resources :route-name :resources]}))

(def service
  #::http{:routes (service-tools/watch-routes-fn #'routes)
          :type :jetty
          :join? false
          :secure-headers {:content-security-policy-settings {:script-src "'unsafe-inline' 'unsafe-eval' 'self'"}}
          :port 9001})

(defonce server (atom nil))

(defn start!
  "Start the development server. Use `stop!` to stop the server after it has
  been started without shutting off the REPL. `server` stores the running server
  instance."
  []
  (reset! server
          (-> service http/default-interceptors http/dev-interceptors http/create-server http/start)))

(defn stop!
  "Stop the development server."
  []
  (swap! server (fn [server]
                  (when server
                    (http/stop server))
                  nil)))

(defn -main
  []
  (start!))
