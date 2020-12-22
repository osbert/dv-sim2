(ns dvorak.translator
  (:use [clojure.string :only [lower-case upper-case]]))

(def keyboards
  {:qwerty  "qwertyuiopasdfghjkl;'zxcvbnm,./"
   :dvorak  "',.pyfgcrlaoeuidhtns-;qjkxbmwvz"
   :colemak "qwfpgjluy;arstdhneio'zxcvbkm,./"})

(defn make-table
  [from-kw to-kw]
  (zipmap (from-kw keyboards)
          (to-kw keyboards)))

(defn reverse-table
  [table]
  (zipmap (vals table) (keys table)))

(defn char-replace
  [table char]
  (let [s (str char)]
    (or (table s)
        (let [table-lower-s (table (lower-case s))]
          (and table-lower-s (upper-case table-lower-s)))
        s)))

(defn convert
  [table s]
  (clojure.string/join (map (partial char-replace table) s)))

