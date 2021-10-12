(ns pingpong.main
  (:require [reagent.core :as r]
            [ajax.core :as ajax]
            [reagent.dom :as dom]
            [re-frame.core :as rf]
            ;; requiring for side-effects
            [day8.re-frame.http-fx]))


(rf/reg-event-db ::initialize-db
                 (fn [db _]
                   (if db
                     db
                     {::pongs []})))

(rf/reg-event-db
 ::pong
 (fn [db [_ result]]
   (update db ::pongs #(conj % result))))

(rf/reg-event-db
 ::error
 (fn [db [_ result]]
   (assoc db ::error result)))

(defn ping
  "Ping the API, dispatching `::pong` on success or `::error` on failure."
  [cofx [_ value]]
  {:db (dissoc (:db cofx) ::pong ::error)
   :http-xhrio {:method :get
                :uri (str "http://localhost:9001/api/ping?value=" value)
                :request-format (ajax/json-request-format {:keywords? true})
                :response-format (ajax/json-response-format {:keywords? true})
                :on-success [::pong]
                :on-failure [::error]}})

(rf/reg-event-fx ::ping ping)

(rf/reg-sub
 ::pongs
 (fn [db _query]
   (get-in db [::pongs])))

(rf/reg-sub
 ::error
 (fn [db _query]
   (get-in db [::error :status-text])))

(defn ^:dev/after-load clear-subs!
  "Clear subscription cache after a hot reload. Otherwise changing subscription
  definitions would do nothing."
  []
  (rf/clear-subscription-cache!))

(defn <root>
  []
  (r/with-let [pongs (rf/subscribe [::pongs])
               error (rf/subscribe [::error])
               input-value (r/atom "")]
    [:div
     [:input {:value @input-value :on-change #(reset! input-value (-> % .-target .-value))}]
     [:button {:on-click #(rf/dispatch [::ping @input-value])} "Ping!"]
     (when @pongs
       [:ul
        (for [[i pong] (map-indexed vector @pongs)]
          ^{:key i} [:li (str  (:ping pong) " : " (:name pong))])])
     (when @error
       [:div {:style {:color "red"}} "Unable to ping: " @error])]))

(defn ^:dev/after-load main
  []
  (rf/dispatch-sync [::initialize-db])
  (dom/render [<root>] (.getElementById js/document "root")))