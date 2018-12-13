(ns damel.model.workers
  (:require [damel.kobaian :as kobaian]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.alpha :as s]))

(def workers-0
  {:workers  {}
   :commands []
   :errors   []})

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
                  :state             :idle})))

(defn get-commands [workers] (:commands workers))


(defn emit [workers command]
  (update workers :commands conj command))

(defn emit-error [workers command reason]
  (update workers :errors conj {:command command
                                :workers workers
                                :reason  reason}))

(defn spawn
  [workers [_ {:keys [id] :as worker}]]
  (-> workers
      (assoc-in [:workers id] worker)
      (emit [:added-worker worker])))

(defn unique
  [[x & others]]
  (when (not (seq others)) x))

(defn find-worker-by-id
  [workers worker-id]
  (get-in workers [:workers worker-id]))

(defn enter-elevator
  [workers [_ worker-id :as command]]
  (if-let [worker (find-worker-by-id workers worker-id)]
    (-> workers
        (update-in [:workers worker-id :state] :entering-elevator)
        (emit [:ordered-to-elevator worker]))
    (emit-error workers command (str "worker id " worker-id " not found"))))

(defn add-command
  [workers [command-type :as command]]
  (println "Received" command-type)
  (case command-type
    :spawn (spawn workers command)
    :enter-elevator (enter-elevator workers command)))

(defn commands-processed
  [workers]
  (update workers :commands empty))