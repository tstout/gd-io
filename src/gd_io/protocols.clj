(ns gd-io.protocols)

(defprotocol IDrive
  (mkdir [drive dir-name]
    "Create a directory, dir-name is a traditional
     unix-style path /a/b/c")

  (ls [drive] "List all root-level directories")

  (upload [drive opts]
    "Upload a file. opts should be a map of
    { :title          - name of file
      :parent-folder  - unix-style path /a/b/c
      :file           - source file path}")

  (download [drive file-id dest-file]
    "Write the file specified by file-id to dest-file")

  (rm [drive file-id] "delete a file"))