(ns pingpong.core-test
  (:require [clojure.test :refer [deftest is]]
            [ring.util.http-response :as response]
            [pingpong.core :as sut]
            [db :as db]))

(def sample-users {1 {:id 1, :first-name "Herry"}
                   2 {:id 2, :first-name "John"}})

(deftest pong-response
  (binding [db/fetch-user (fn [id]
                            (get sample-users id))]
    (is (= (response/ok {:ping "pong" :name "Herry"}) (sut/pong {}))
        "it should pong and return first user name")
    (is (= (response/ok {:ping "pong" :name "John"}) (sut/pong {}))
        "it should pong and return last user name")
    (is (= (response/ok {:ping "pong" :name "Herry"}) (sut/pong {}))
        "it should pong and return first user name")))
