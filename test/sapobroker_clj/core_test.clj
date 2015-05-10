(ns sapobroker-clj.core-test
  (:require [clojure.test :refer :all]
            [sapobroker-clj.core :refer :all]))


(deftest connect-default
  (testing "connecting with default params"
    (is (= (type (connect)) pt.com.broker.client.BrokerClient))))
