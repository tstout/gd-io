(ns gd-io.config
  (:require [clojure.java.io :refer [as-file file make-parents]]
            [clojure.edn :as edn]))

(def cfg-defaults
  {:root-dir (System/getProperty "user.home")
   :dir      ".gd-io"
   :fname    "gd-io-creds.clj"})

(defn cfg-file [opts]
  (let [{:keys [root-dir dir fname]}
        (merge cfg-defaults opts)]
    (file root-dir dir fname)))

(def stub-config
  {:client-id     "YOUR CLIENT ID"
   :client-secret "YOUR CLIENT SECRET"
   :redirect-uris ["urn:ietf:wg:oauth:2.0:oob" "http://localhost"]
   :auth-map      {:access-token  "YOUR ACCESS TOKEN"
                   :expires-in    3600
                   :refresh-token "YOUR REFRESH TOKEN"
                   :token-type    "Bearer"}})

(defn mk-config [opts]
  (let [file (cfg-file opts)]
    (when-not (.exists file)
      (make-parents file)
      (spit file stub-config))))

(defn load-config [opts]
  (mk-config opts)
  (->
    (cfg-file opts)
    slurp
    edn/read-string))

(defn load-default-config []
  (load-config {}))


