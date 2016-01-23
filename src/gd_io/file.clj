(ns gd-io.file
  "Google Drive I/O library"
  (:require [gd-io.config :refer [load-default-config]]
            [gd-io.internal :refer [mk-drive-service
                                    root-folder
                                    get-folders
                                    insert-folder
                                    insert-file
                                    mk-file-meta
                                    download-file
                                    trash-file]]
            [gd-io.protocols :refer [IDrive]]
            [clojure.java.io :refer [output-stream file]]
            [clojure.string :as str]))

(defn file-by-id [id files]
  (first
    (filter #(= (:id %) id) files)))

(defn file-title [id files]
  (vector (:title (file-by-id id files)) id))

(defn dir-title [id files]
  (if-let [title (file-title id files)]
    (vector title id)
    (vector "/" id)))

(defn dir-titles [id-vec files]
  (map #(dir-title % files) id-vec))

(defn mk-parent-info [parents]
  (mapv #(.getId %) parents))

(defn file-titles [files]
  (map #(file-title (:id %) files) files))

(defn in?
  "true if the sequence contains the element"
  [seq elm]
  (some #(= elm %) seq))

(defn not-blank? [str]
  (not (str/blank? str)))

(defn normalize-dir-tree [dtree dirs]
  (for [[root-dirs child-dirs] dtree]
    (vector (dir-titles root-dirs dirs) (file-titles child-dirs))))

(defn ls-dirs [drive]
  (map
    #(hash-map
      :title (.getTitle %)
      :id (.getId %)
      :parents (vec (mk-parent-info (.getParents %))))
    (get-folders drive)))

(defn child-dirs [dirs parent-id]
  (filter #(in? (:parents %) parent-id) dirs))

(defn dir-by-title [dirs title index]
  (if-let [existing-dir (first (filter #(= (:title %) title) dirs))]
    (assoc existing-dir :index index)
    {:title title
     :id    nil
     :index index}))

(defn mk-path [dirs path]
  (let [parts (filter #(not-blank? %) (str/split path #"/"))]
    (vec (map-indexed
           (fn [index itm] (dir-by-title dirs itm index)) parts))))

(defn dir-exists? [dirs path]
  (not-any?
    #(nil? (:id %))
    (mk-path dirs path)))

(defn mk-folder [drive name parent-id]
  (insert-folder
    drive
    {:title         name
     :description   ""
     :media-type    "application/vnd.google-apps.folder"
     :parent-folder parent-id}))

(defn only-one? [coll]
  (= 1 (count coll)))

;;
;; Note: It seems a more elegant implementation could be achieved here.
;; The gist is that each node in the path is data that should
;; ultimately be reduced to a single gdrive file id. I think the design
;; is sound, but my implementation might be juvenile.
;;
(defn mk-dir
  "Create the specified directory along with any parent directories
  if they do not currently exist. If the directory already exists, this
  is not considered an error. A map containing GDrive information for the
  path is returned. The path argument is a typical unix-style path. For example,
  /family-pictures/2015"
  [drive path]
  (let [root-folder (root-folder drive)
        dirs (ls-dirs drive)
        nodes (mk-path dirs path)]
    (if (only-one? nodes)
      (if-not (dir-exists? dirs path)
        {:id (mk-folder drive (:title (first nodes)) root-folder)}
        (first nodes))
      (reduce
        (fn [parent current]
          (cond
            (nil? (:id parent))
            ;;
            ;; Parent did not exist, need to create it, along with current
            ;;
            (let [parent-id (mk-folder drive (:title parent) root-folder)]
              {:id (mk-folder drive (:title current) parent-id)})
            (nil? (:id current))
            ;;
            ;; Parent already exists, simply create current
            ;;
            {:id (mk-folder drive (:title current) (:id parent))}
            ;;
            ;; Both parent and current exist, simply return current
            ;;
            :else current))
        (sort-by :index nodes)))))

(defrecord GDrive [drive]
  IDrive
  (mkdir [this dir-name]
    (mk-dir drive dir-name))

  (ls [this]
    (ls-dirs drive))

  (upload [this opts]
    (let [{:keys [title parent-folder file]} opts]
      (insert-file {:drive     drive
                    :file-meta (mk-file-meta
                                 {:title         title
                                  :description   "auto-gen"
                                  :media-type    "application/octet-stream"
                                  :parent-folder (:id (mk-dir drive parent-folder))})
                    :file      file})))

  (download [this file-id dest-file]
    (with-open [ostream (output-stream dest-file)]
      (download-file drive file-id ostream))
    (file dest-file))

  (rm [this file-id]
    (trash-file drive file-id)))

(defn mk-gdrive [& opts]
  (let [{:keys [app-name] :or {app-name "gd-io"}} opts]
    (->GDrive
      (mk-drive-service (load-default-config) app-name))))

