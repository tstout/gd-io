(ns gd-io.protocols)

(defprotocol IDrive
  (mkdir [drive dir-name] "Create a directory")
  (ls [drive] "List directories")
  (cp [drive src-file dest-file] "Copy a file")
  (rm [drive file] "Delete a file"))
