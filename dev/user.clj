(ns user
  (:require
    [clojure.inspector :refer [inspect inspect-tree inspect-table]]
    [clojure.java.shell :refer [sh]]
    [clojure.data :refer [diff]]
    [gd-io.protocols :refer [ls upload download mkdir]]
    [gd-io.internal :refer [mk-drive-service
                            mk-file-meta
                            insert-file
                            trash-file
                            get-files
                            get-folders
                            about
                            about-summary
                            root-folder
                            insert-folder
                            download-file]]
    [gd-io.config :refer [load-default-config]]
    [clojure.string :as str]
    [gd-io.file :refer [ls-dirs
                        dir-tree
                        unique-parents
                        file-by-id
                        file-title
                        normalize-dir-tree
                        dir-titles
                        path-vec
                        in?
                        child-dirs
                        file-titles
                        mk-path
                        mk-dir
                        dir-by-title
                        dir-exists?
                        mk-gdrive]]
    [clojure.java.io :refer [as-file
                             resource
                             file
                             make-parents
                             input-stream]]))


(println "--- loading user.clj ---")

(def driveservice
  (mk-drive-service
    (load-default-config)
    "fin-kratzen"))

;;(def dirs (ls-dirs driveservice))
;;(def dtree (dir-tree dirs))
;;(def ntree (normalize-dir-tree dtree dirs))

(def home-dir (System/getProperty "user.home"))
;;(def ~ (System/getProperty "user.home"))

(def drive (mk-gdrive "fin-kratzen"))

(def test-file (file (resource "test-data.txt")))

(def test-file-meta
  {:drive     driveservice
   :file-meta (mk-file-meta {:title         "bkup-test-file2"
                             :description   "This is a test"
                             :media-type    "application/octet-stream"
                             :parent-folder "0ABqBLjW50GqhUk9PVA"})
   :file      test-file})

(def test-file (file (resource "test-data.txt")))

;(defn prn-data [data-stream]
;  (with-open [r (input-stream (byte-array [0xDE 0xAD 0xBE 0xEF]))]
;    (loop [c (.read r)]
;      (when-not (= -1 c)
;        (do
;          (print "iteration...")
;          (print (char c))
;          (recur (.read r)))))))
