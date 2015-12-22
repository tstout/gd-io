(ns gd-io.file-test
  "Integration test for file operations"
  (:require [gd-io.file :refer [mk-gdrive]]
            [clojure.java.io :refer [file resource]]
            [gd-io.protocols :refer [ls rm upload download mkdir]]
            [expectations :refer [expect expect-let]]))

(def gdrive (mk-gdrive "gd-io test"))
(def tfile (file (resource "test-data.txt")))
(def state (atom {:file-id nil}))

;;
;; Upload a file, download it, and then delete it.
;;
(defn before
  {:expectations-options :before-run}
  []
  (let [id (upload gdrive {:title         "gd-io-test.txt"
                           :parent-folder "/gd-io/test"
                           :file          tfile})]
    (swap! state assoc :file-id id)))

(defn after
  {:expectations-options :after-run}
  []
  (rm gdrive (:file-id @state)))

(expect not-empty (ls gdrive))

(expect 1 1)
