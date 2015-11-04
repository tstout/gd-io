(ns gd-io.core
  "Google Drive I/O library"
  (:require [gd-io.config :refer [load-config]]
            [gd-io.interop :refer [mk-drive-service
                                   root-folder
                                   insert-folder]]
            [gd-io.protocols :refer [IDrive]]
            [clojure.string :as str]))

(defrecord FileId [parent-id name id])

;; TODO - move to interop
(def ^:private queries
  {:folders "mimeType = 'application/vnd.google-apps.folder' and trashed=false"
   :parents "'%s'in parents"})

(defn dir-exists? [])

(defn file-by-id [id files]
  (first
    (filter #(= (:id %) id) files)))

(defn file-title [id files]
  (vector (:title (file-by-id id files)) id))

(defn dir-title [id files]
  (if-let [title (file-title id files)]
    (vector title id)
    (vector "/" id)))

;(defn root-dir-id [dirs]
;  (filter #(= nil (file-title) dirs)

(defn dir-titles [id-vec files]
  (map #(dir-title % files) id-vec))

(defn mk-parent-info [parents]
  (mapv #(.getId %) parents))

(defn file-titles [files]
  (map #(file-title (:id %) files) files))

;; TODO move to interop
(defn get-file-by-id [drive-service id]
  (->
    drive-service
    (.files)
    (.get id)
    (.execute)))

;; TODO move to interop
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

;; TODO move to interop
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

(defn in?
  "true if the sequence contains the element"
  [seq elm]
  (some #(= elm %) seq))

(defn not-blank? [str]
  (not (str/blank? str)))

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

(defn child-dirs [dirs parent-id]
  (filter #(in? (:parents %) parent-id) dirs))

(defn dir-by-title [dirs title index]
  (if-let [existing-dir (first (filter #(= (:title %) title) dirs))]
    (assoc existing-dir :index index)
    {:title title
     :id    nil
     :index index}))

(defn path-parent-id
  "Extract the id of the parent path corresponding to the
  path-node"
  [path-node path-vec]
  {:pre [(map? path-node)
         (> (:index path-node) 0)]}
  (let [parent-index (dec (:index path-node))]
    (:id
      (first
        (filter
          #(= parent-index (:index %))
          path-vec)))))

(defn mk-path [dirs path]
  (let [parts (filter #(not-blank? %) (str/split path #"/"))]
    (vec (map-indexed
           (fn [index itm] (dir-by-title dirs itm index)) parts))))

(defn dir-exists? [dirs path]
  (not-any?
    #(nil? (:id %))
    (mk-path dirs path)))


(defn folder-meta [name parent-id]
  )

(defn mk-folder [drive-service name parent-id]
  (insert-folder
    drive-service
    {:title         name
     :description   ""
     :media-type    "application/vnd.google-apps.folder"
     :parent-folder parent-id}))

(defn mk-dir
  "Create the specified directory along with any parent directories
  if they do not currently exist. If the directory already exists, this
  is not considered an error. The GDrive id of the path is returned.
  The path argument is a typical unix-style path"
  [drive path]
  (let [nodes (mk-path (ls-dirs drive) path)]
    (if (= 1 (count nodes))
      {:id (mk-folder drive (:title (first nodes)) (root-folder drive))}
      (reduce
        (fn [parent current]
          (cond
            (nil? (:id parent))
            (let [parent-id (mk-folder drive (:title parent) (root-folder drive))]
              {:id (mk-folder drive (:title current) parent-id)})
            (nil? (:id current))
            {:id (mk-folder drive (:title current) (:id parent))}
            :else current))
        (sort-by :index nodes)))))


  (defn ls-dir [drive-service filter]
    (ls-dirs drive-service))

  (defn cp-file [drive src-file dest-file])

  (defrecord GDrive [drive-service]
    IDrive
    (mkdir [drive dir-name]
      ;;(mk-dir drive-service dir-name))
      )

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
