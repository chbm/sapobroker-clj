(ns sapobroker-clj.core-test
  (:require [clojure.test :refer :all]
            [sapobroker-clj.core :refer :all]))


(deftest connect-default
  (testing "connecting with default params"
    (let [bc (start-client)]
      (is (instance? pt.com.broker.client.nio.BrokerClient bc))
      (is (is-connected? bc))
      )
    ))

(deftest pub-topic
  (testing "publish to a topic"
   (is (future? (publish-topic (start-client) "/teste" "ola")))))

(deftest pub-sub
  (testing "publish and subscribe to a queue"
    (let [bcpub (start-client)
          bcsub (start-client)
          thequeue (->> (fn [] (rand-nth (map char (range 33 127))))
                        repeatedly
                        (take 16)
                        (apply str))
          result (promise)
          subscription (subscribe-queue bcsub thequeue 
                        (fn check-res [m]
                          (let [message (notification-as-string m)]
                          (if (= message "itsalive")
                            (deliver result true)
                            (deliver result false))
                          @result)))
          published (publish-queue bcpub thequeue "itsalive")]
     ;;; (future (Thread/sleep 10000) (deliver result false))
      (is (instance? pt.com.broker.client.nio.server.HostInfo @subscription))
      (is (instance? pt.com.broker.client.nio.server.HostInfo @published))
      (is (= @result true))
      )
    ))


(deftest pub-sub
  (testing "publish and subscribe to a queue"
    (let [bcpub (start-client)
          bcsub (start-client)
          result (promise)
          subscription (subscribe-topic bcsub "/testingclojure" 
                        (fn check-res [m]
                          (let [message (notification-as-string m)]
                          (if (= message "itsalive")
                            (deliver result true)
                            (deliver result false))
                          @result)))
          published (publish-topic bcpub "/testingclojure" "itsalive")]
     ;;; (future (Thread/sleep 10000) (deliver result false))
      (is (instance? pt.com.broker.client.nio.server.HostInfo @subscription))
      (is (instance? pt.com.broker.client.nio.server.HostInfo @published))
      (is (= @result true))
      )
    ))


