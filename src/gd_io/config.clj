(ns gd-io.config
  (:require [clojure.java.io :refer [as-file file make-parents]]
            [clojure.edn :as edn]))

(defn cfg-file
  ([dirname fname]
   (->
     (System/getProperty "user.home")
     (file dirname fname))))

(def default-cfg-file (cfg-file ".gd-io" "gd-io-creds.clj"))

(def stub-config
  {:client-id     "YOUR CLIENT ID"
   :client-secret "YOUR CLIENT SECRET"
   :redirect-uris ["urn:ietf:wg:oauth:2.0:oob" "http://localhost"]
   :auth-map      {:access-token  "YOUR ACCESS TOKEN"
                   :expires-in    3600
                   :refresh-token "YOUR REFRESH TOKEN",
                   :token-type    "Bearer"}})

;; TODO - change this to throw an exception indicating
;; where and what stub file was created...
(defn mk-config []
  (when-not (.exists default-cfg-file)
    (do
      (make-parents default-cfg-file)
      (spit default-cfg-file stub-config))))

(defn load-config []
  (mk-config)
  (->
    default-cfg-file
    slurp
    edn/read-string))


