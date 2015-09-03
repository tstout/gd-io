(ns gd-io.core
  "Google Drive I/O library"
  (:import
           (com.google.api.client.googleapis.auth.oauth2 GoogleClientSecrets$Details
                                                         GoogleCredential
                                                         GoogleCredential$Builder
                                                         GoogleTokenResponse GoogleClientSecrets)
           (com.google.api.client.googleapis.javanet GoogleNetHttpTransport)
           (com.google.api.client.json.jackson2 JacksonFactory)
           (com.google.api.services.drive Drive Drive$Builder)
           (com.google.api.services.drive.model File))

  (:require [gd-io.config :refer [load-config]]))

(def ^:private http-transport
  (GoogleNetHttpTransport/newTrustedTransport))

(def ^:private json-factory
  (JacksonFactory/getDefaultInstance))

(defn mk-token-response [{{:keys [access-token refresh-token token-type]} :auth-map}]
  (doto
    (GoogleTokenResponse.)
    (.setAccessToken access-token)
    (.setRefreshToken refresh-token)
    (.setTokenType token-type)))

(defn mk-secret [{:keys [client-id client-secret redirect-uris]}]
  (doto
    (GoogleClientSecrets.)
    (.setInstalled (doto
                     (GoogleClientSecrets$Details.)
                     (.setClientId client-id)
                     (.setRedirectUris redirect-uris)
                     (.setClientSecret client-secret)))))

(defn ^GoogleCredential mk-credential [ctx]
  (->
    (doto
      (GoogleCredential$Builder.)
      (.setTransport http-transport)
      (.setJsonFactory json-factory)
      (.setClientSecrets (mk-secret ctx)))
    (.build)
    (.setFromTokenResponse (mk-token-response ctx))))

(defn ^Drive mk-drive-service [ctx]
  (->>
    ctx
    (mk-credential)
    (Drive$Builder. http-transport json-factory)
    (.build)))

(defn get-files
  "Put some good docs here..."
  []
  (->
    (mk-drive-service (load-config))
    (.files)
    (.list)
    (.setQ "mimeType = 'application/vnd.google-apps.folder'")
    (.execute)
    (.getItems)))

(defn mk-parent-info [parents]
  (map #(hash-map
         :id (.getId %)
         :is-root (.getIsRoot %)) parents))

(defn ls-dirs []
  (map
    #(hash-map
      :title (.getTitle %)
      :id (.getId %)
      :parents (vec (mk-parent-info (.getParents %))))
    (get-files)))