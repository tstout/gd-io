(ns gd-io.config
  (:require [clojure.java.io :refer [as-file file make-parents]]
            [clojure.edn :as edn]))

(defn cfg-file
  ([]
   (cfg-file ".gd-io" "gd-io-creds.clj"))

  ([dirname fname]
   (->
     (System/getProperty "user.home")
     (file dirname fname))))

(def ^:private stub-config
  {:client-id     "YOUR CLIENT ID"
   :client-secret "YOUR CLIENT SECRET"
   :redirect-uris ["urn:ietf:wg:oauth:2.0:oob" "http://localhost"]
   :auth-map      {:access-token  "YOUR ACCESS TOKEN"
                   :expires-in    3600
                   :refresh-token "YOUR REFRESH TOKEN",
                   :token-type    "Bearer"}})

(defn mk-config []
  (when-not (.exists (cfg-file))
    (do
      (make-parents (cfg-file))
      (spit (cfg-file) stub-config))))

(defn load-config
  ([]
   (mk-config)
   (->
     (cfg-file)
     slurp
     edn/read-string)))


