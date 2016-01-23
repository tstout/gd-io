(ns gd-io.protocols)

(defprotocol IDrive
  (mkdir [drive dir-name]
    "Create a directory, dir-name is a traditional
     unix-style path /a/b/c
     If any directories in the path do not exist, they
     will be created.")

  (ls [drive] "List all root-level directories")

  (upload [drive opts]
    "Upload a file. opts should be a map of
    { :title          - name of file
      :parent-folder  - unix-style path /a/b/c
      :file           - source file path}

    Returns the id of the new file")

  (download [drive file-id dest-path]
    "Write the file specified by file-id to dest-path")

  (rm [drive file-id] "delete a file"))