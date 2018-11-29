(ns damel.kobaian
  (:require #?(:cljs [cljs.spec.alpha :as s]
               :clj  [clojure.spec.alpha :as s])
            [clojure.string :as str]
            [clojure.test.check.generators :as gen]))

(def vowels #{"a" "é" "ō" "ai" "ou" "i"})

(def consonants #{"k" "h" "m" "n" "t" "y"})

(s/def :kobaian/syllabus (s/cat :conson consonants :vowel vowels))

(s/def :kobaian/word (s/coll-of :kobaian/syllabus :min-count 1 :max-count 5))

(s/def :kobaian/sentence (s/cat :words (s/+ :kobaian/word) :end #{"?" "!" "."}))

(defn syllabus->str [[v c]] (str v c))

(defn word->str [word] (apply str (map syllabus->str word)))

(defn sentence->str
  [sentence]
  (str/join " "
            (map (fn [token]
                   (cond
                     (vector? token) (word->str token)
                     :else token))
                 sentence)))

(defn make-sentence
  []
  (-> (s/gen :kobaian/sentence)
      (gen/generate)
      (sentence->str)))