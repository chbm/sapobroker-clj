(ns sapobroker-clj.core
  (:import pt.com.broker.client.messaging.BrokerListener
           [pt.com.broker.client BrokerClient]
           [pt.com.broker.types NetAction NetNotification NetProtocolType NetSubscribe])
  )

(defn connect
  [& {:keys [server port] :or {server "broker.labs.sapo.pt" port 3323
                             appname "clojure" ptype NetProtocolType/PROTOCOL_BUFFER}}]
  (try 
    (pt.com.broker.client.BrokerClient. server port)
    (catch Exception e 
      (println e))))

(defn addserver
   [^BrokerClient bc {:keys [server port] :or {:port 3323}}]
   (doto bc (.addServer :server :port))
   )

(defn subscribe
  [^BrokerClient bc dest ^pt.com.broker.types.NetAction$DestinationType destType fn]
  (let [list (proxy [BrokerListener] []
              (onMessage
                [^NetNotification message]
                (fn message))
              (isAutoAck 
                []
                true))
        subs (.new NetSubscribe dest destType)]
    (doto bc (.addAsyncConsumer subs))))



(defn subsTopic
  [^BrokerClient bc dest fn]
  (subscribe bc dest pt.com.broker.types.NetAction$DestinationType/TOPIC fn))

(defn subsQueue
  [^BrokerClient bc dest fn]
  (subscribe bc dest pt.com.broker.types.NetAction$DestinationType/QUEUE fn))

(defn publish
  [^BrokerClient bc dest ^pt.com.broker.types.NetAction$DestinationType destType message]
  (doto bc (.publish message dest destType)))



