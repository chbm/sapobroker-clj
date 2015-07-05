(ns sapobroker-clj.core
  (:import [pt.com.broker.client.nio BrokerClient]
           [pt.com.broker.client.nio.events BrokerListener NotificationListenerAdapter]
           [pt.com.broker.types NetAction NetNotification NetProtocolType NetSubscribe]
           [pt.com.broker.client.nio.server HostInfo])
  )

(defn- dest-type 
  [t]
  (pt.com.broker.types.NetAction$DestinationType/valueOf (name t)))

(defn notification-as-byte
  "gets the payload from a NetNotification"
  [^pt.com.broker.types.NetNotification notif]
  (.getPayload (.getMessage notif)))

(defn notification-as-string
  "gets the payload from a NetNotification as a string"
  [^pt.com.broker.types.NetNotification notif]
  (String. (notification-as-byte notif)))

(defn start-client
  "creates a new broker client connected to the given server and port. defaults to broker.labs.sapo.pt"
  [& {:keys [server port] :or {server "broker.labs.sapo.pt" port 3323
                               appname "clojure" ptype NetProtocolType/PROTOCOL_BUFFER}}]
  (let [bc (pt.com.broker.client.nio.BrokerClient. server port)]
    (try
      (doto bc (.connect))
      (catch Exception e nil))
    ))

(defn is-connected? 
  "Returns a promise to the boolean state of the broker client"
  [^BrokerClient bc]
  (let [p (promise)
        bl (proxy [pt.com.broker.client.nio.events.PongListenerAdapter] []
             (onMessage [pong host]
               (deliver p true)))]
    (try
      (.checkStatus bc (cast pt.com.broker.client.nio.events.BrokerListener bl))
      (catch Exception e
        (deliver p false)))
    p
    ))

(defn add-server
  "adds new server connection to the client"
  [^BrokerClient bc {:keys [server port] :or {:port 3323}}]
  (try
    (doto bc (.addServer :server :port) (.connect))
    (catch Exception e nil) ;;; XXX better handling here
    ))



(defn subscribe
  "Subscribe to a destination. fn is called with a pt.com.broker.types.NetNotification"
  [^BrokerClient bc 
   destType 
   ^String dest 
   fn]
  (let [listener (proxy [NotificationListenerAdapter] []
                   (onMessage
                     [^NetNotification message
                      ^HostInfo host]
                     (fn message))
                   (isAutoAck 
                     []
                     true))]
    (try
      (.subscribe bc dest (dest-type destType) listener)
      (catch Exception e (do (println (.getMessage e)) nil))) ;;; XXX better handling
    ))

(defn subscribe-topic
  [^BrokerClient bc dest fn]
  (subscribe bc :TOPIC dest fn))

(defn subscribe-queue
  [^BrokerClient bc dest fn]
  (subscribe bc :QUEUE dest fn))

(defn publish
  [^pt.com.broker.client.nio.BrokerClient bc 
   ^pt.com.broker.types.NetAction$DestinationType destType 
   ^String dest 
   ^String message]
  (.publish bc message dest (dest-type destType)))


(defn publish-topic
  [^BrokerClient bc dest message]
  (publish bc :TOPIC dest message))


(defn publish-queue
  [^BrokerClient bc dest message]
  (publish bc :QUEUE dest message))


