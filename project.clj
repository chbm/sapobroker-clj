(defproject sapobroker-clj "0.1.0"
  :description "Bindings for the SAPO Broker java-nio client"
  :url "https://github.com/chbm/sapobroker-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [pt.sapo.oss.broker/sapo-broker-java-client-nio "4.0.50.Alpha4"]]
  :repositories [["sapo" {:url "http://repository.sl.pt/nexus/content/groups/public/"}]])
