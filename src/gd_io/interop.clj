(ns gd-io.interop
  (:import
    (com.google.api.client.googleapis.auth.oauth2 GoogleClientSecrets$Details
                                                  GoogleCredential
                                                  GoogleCredential$Builder
                                                  GoogleTokenResponse GoogleClientSecrets)
    (com.google.api.client.googleapis.javanet GoogleNetHttpTransport)
    (com.google.api.client.json.jackson2 JacksonFactory)
    (com.google.api.services.drive Drive Drive$Builder)
    (com.google.api.services.drive.model File ParentReference)
    (com.google.api.client.http FileContent))

  (:require [gd-io.config :refer [load-config]]
            [clojure.set :as set]))

(def ^:private mime-type
  {:file   "application/vnd.google-apps.file"
   :folder "application/vnd.google-apps.folder"})

(def ^:private http-transport
  (GoogleNetHttpTransport/newTrustedTransport))

(def ^:private json-factory
  (JacksonFactory/getDefaultInstance))

(defn has-keys? [m key-seq]
  (set/subset? (set key-seq) (set (keys m))))

(defn mk-token-response [{{:keys [access-token
                                  refresh-token
                                  token-type]} :auth-map}]
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

(defn ^Drive mk-drive-service [ctx app-name]
  (let [builder (->>
                  ctx
                  (mk-credential)
                  (Drive$Builder. http-transport json-factory))]
    (->
      builder
      (.setApplicationName app-name)
      (.build))))

(defn ^File mk-file-meta [{:keys [title
                                  description
                                  media-type
                                  parent-folder] :as m-arg}]
  {:pre [(has-keys? m-arg [:title
                           :description
                           :media-type
                           :parent-folder])]}
  (->
    (File.)
    (.setTitle title)
    (.setDescription description)
    (.setMimeType media-type)
    (.setParents (vector (->
                           (ParentReference.)
                           (.setId parent-folder))))))

(defn insert-file
  "Upload a file given a map of the requisite stuff.
  The ID of the new file is returned"
  [{:keys [drive-service
           file-meta
           file] :as m-arg}]
  {:pre [(has-keys? m-arg [:drive-service
                           :file-meta
                           :file])]}
  (->
    drive-service
    (.files)
    (.insert file-meta (FileContent. (.getMimeType file-meta) file))
    (.execute)
    (.getId)))

(defn trash-file
  "Move a file to the trash.
  The ID of the trashed file is returned"
  [drive-serice file-id]
  (->
    drive-serice
    (.files)
    (.trash file-id)
    (.execute)
    (.getId)))

(defn about [drive-service]
  (->
    drive-service
    (.about)
    (.get)
    (.execute)
    (bean)))

(defn about-summary
  "Google Drive's about endpoint returns a copious amount of info.
  This merely returns a very useful, and very small subset"
  [drive-service]
  (let [{:keys [rootFolderId
                quotaBytesUsed
                quotaBytesTotal]} (about drive-service)]
    {:root-folder           rootFolderId
     :quota-bytes-used      quotaBytesUsed
     :quota-bytes-total     quotaBytesTotal
     :quota-bytes-remaining (- quotaBytesTotal quotaBytesUsed)}))

(defn root-folder
  "Convenience fn for extracting the root folder from a drive service instance"
  [drive-service]
  (:root-folder (about-summary drive-service)))

