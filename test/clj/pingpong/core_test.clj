(ns pingpong.core-test
  (:require [clojure.test :refer [deftest is]]
            [ring.util.http-response :as response]
            [pingpong.core :as sut]
            [db :as db]))
