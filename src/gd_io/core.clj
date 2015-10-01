(ns gd-io.core
  "Google Drive I/O library"
  (:require [gd-io.config :refer [load-config]]
            [gd-io.interop :refer [mk-drive-service]]
            [gd-io.protocols :refer [IDrive]]
            [clojure.string :as str]))

(defrecord FileId [parent-id name id])

(def ^:private queries
  {:folders "mimeType = 'application/vnd.google-apps.folder' and trashed=false"
   :parents "'%s'in parents"})

(defn dir-exists? [])

(defn file-by-id [id files]
  (first
    (filter #(= (:id %) id) files)))

(defn file-title [id files]
  (:title (file-by-id id files)))

(defn dir-title [id files]
  (if-let [title (file-title id files)]
    title
    "/"))

(defn dir-titles [id-vec files]
  (map #(dir-title % files) id-vec))

(defn mk-parent-info [parents]
  (mapv #(.getId %) parents))

(defn file-titles [files]
  (map #(file-title (:id %) files) files))

(defn get-file-by-id [drive-service id]
  (->
    drive-service
    (.files)
    (.get id)
    (.execute)))

(defn get-files
  "get files matching a gdrive query"
  [drive-service query]
  []
  (->
    drive-service
    (.files)
    (.list)
    (.setQ query)
    (.execute)
    (.getItems)))

(defn get-folders [drive-service]
  (get-files drive-service (:folders queries)))

(defn unique-parents [dirs]
  ;; TODO - find file names of these IDs.
  ;; simply need to find the parent id name. Might have to map
  ;; roots to "/" explicitly.
  (set (map #(:parents %) dirs)))

;(defn dir-tree [dirs]
;  (let [parents (unique-parents dirs)]
;    (map #() )

(defn path-vec [path]
  (vec (str/split path #"/")))

(defn dir-tree [dirs]
  (group-by :parents dirs))

(defn dir-exists [path dirs]
  (let [dtree (dir-tree dirs)
        path-vec (str/split path #"/")]
    ;;
    ;; use zipmap here to match?
    ;;
    ))


(defn normalize-dir-tree [dtree dirs]
  (for [[root-dirs child-dirs] dtree]
    (vector (dir-titles root-dirs dirs) (file-titles child-dirs))))

(defn ls-dirs [drive-service]
  (map
    #(hash-map
      :title (.getTitle %)
      :id (.getId %)
      :parents (vec (mk-parent-info (.getParents %))))
    (get-folders drive-service)))

(defn mk-dir [drive dir-name]
  (prn "mkdir---?"))

(defn ls-dir [drive-service filter]
  (ls-dirs drive-service))

(defn cp-file [drive src-file dest-file])

(defrecord GDrive [drive-service]
  IDrive
  (mkdir [drive dir-name]
    (mk-dir drive-service dir-name))

  (ls [drive filter]
    (ls-dir drive-service filter))

  (cp [drive src-file dest-file]
    (cp-file drive-service src-file dest-file))

  (rm [drive file]
    (prn "rm---")))

(defn mk-gdrive [app-name]
  (->GDrive
    (mk-drive-service (load-config) app-name)))


;;
;; I considerd using a let-over-lambda style
;; instead of defrecord/defprotocol
;;;
;(defn create-gdrive []
;  (let [drive (mk-drive-service (load-config))
;        operations {:mkdir (fn [dir-name] (mk-dir drive dir-name))
;                    :ls    (fn [_] (ls-dirs drive))
;                    :cp    (fn [src-file dest-file] (cp drive src-file dest-file))
;                    :rm    (fn [file] (rm drive file))}]
;    (fn [operation & params]
;      (->
;        (operation operations)
;        (apply params)))))


;(defn mk-dir [remote-parent-id]
;  )

(defn rm-dir [remote-dir-id]
  )

;(defn upload-file [{:keys [local-file remote-dir-id remote-name]}]
;  )

(defn download-file []
  )
