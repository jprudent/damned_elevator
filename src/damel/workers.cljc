(ns damel.workers
  (:require [damel.kobaian :as kobaian]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.alpha :as s]))

(def workers-0
  {:workers  []
   :commands []})

(defonce max-id (atom 0))
(defn next-id [] (swap! max-id inc))

(defrecord Worker [id
                   name
                   sex
                   age
                   laziness
                   fidelity
                   anxiety
                   arrival-time
                   expected-time
                   current-level
                   destination-level
                   in-elevator?])

(defn ->random-worker
  [current-tick nb-level best-trip-time]
  (println "ooqijf")
  (let [destination-level (rand-int nb-level)
        best-trip-time    (best-trip-time destination-level)]
    (map->Worker {:id                (next-id)
                  :name              (kobaian/make-name)
                  :sex               (gen/generate (s/gen #{:male :female}))
                  :age               (+ 18 (rand-int 70))
                  :laziness          (rand 1)
                  :fidelity          (rand 1)
                  :anxiety           (rand 1)
                  :arrival-time      current-tick
                  :expected-time     (int (+ current-tick best-trip-time (* best-trip-time (rand 1))))
                  :current-level     0
                  :destination-level destination-level
                  :in-elevator?      false})))

(defn get-commands [workers] (:commands workers))

(defn spawn
  [workers [_ worker]]
  (-> workers
      (update :workers conj worker)
      (update :commands conj [:added-worker worker])))

(defn add-command
  [workers [command-type :as command]]
  (case command-type
    :spawn (spawn workers command)))

(defn commands-processed
  [workers]
  (update workers :commands empty))