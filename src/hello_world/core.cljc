(ns hello-world.core
  (:require #?(:cljs [cljs.spec.alpha :as s]
               :clj  [clojure.spec.alpha :as s])
            [clojure.string :as str]))

(println "Hello world!")

;; make a language

(def vowels #{"a" "é" "ō" "ai" "ou" "i"})
(def consons #{"k" "h" "m" "n" "t" "y"})
(s/def :de.language.japanito/syllabus (s/cat :conson consons
                                             :vowel vowels))
(s/def :de.language.japanito/word
  (s/coll-of :de.language.japanito/syllabus
             :min-count 1
             :max-count 5))

(s/def :de.language.japanito/sentence
  (s/+ (s/cat :chunk (s/+ :de.language.japanito/word)
              :separator #{"?" "!" "."})))

(defn pr-str-syllabus
  [[v c :as syllabus]]
  (str v c))

(defn pr-str-word
  [word]
  (apply str (map pr-str-syllabus word)))

(defn pr-str-sentence
  [sentence]
  (str/join " " (map (fn [token]
                       (cond
                         (vector? token) (pr-str-word token)
                         :else token))
                     sentence)))

#_(pr-str-sentence (gen/generate (s/gen :de.language.japanito/sentence)))

;; specs

;(s/def :de.employee/name (s/and string? #(>= (count %) 2)))
;
;(s/def :de.employee (s/keys [:de.employee/name
;                             :de.employee/laziness
;                             :de.employee/satisfaction
;                             :de.employee/time-remaining
;                             :de.employee/shyness]))