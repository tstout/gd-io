(ns gd-io.file-test
  (:require [gd-io.file :refer [mk-gdrive]]
            [gd-io.protocols :refer [ls]]
            [expectations :refer [expect]]))

(def gdrive (mk-gdrive "fin-kratzen"))


(defn before
  {:expectations-options :before-run}
  [])

(expect not-empty (ls gdrive))

