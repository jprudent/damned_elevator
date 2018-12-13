(ns damel.state
  ;; TODO is that OOP ? At least it's encapsulation right ?
  ;; "each function takes a state as first parameter"
  ;; "there is no other place where something know how to read or write the state"
  (:require
    [damel.model.elevator-cabin :as elevator-cabin]
    [damel.model.workers :as workers]))

(def state-0
  {:ticks              0

   ;; game state
   :cabin              elevator-cabin/cabin-0
   :cabin-current-move nil
   :workers            workers/workers-0

   ;; contains only graphical objects
   :objects            {:cabin   nil
                        :workers {}}})

;; cabin stuff
;; -----------

(defn get-cabin-sprite [state] (get-in state [:objects :cabin]))

(defn set-cabin-sprite [state cabin] (assoc-in state [:objects :cabin] cabin))

(defn set-cabin-move
  [state cabin-move]
  (assoc state :cabin-current-move cabin-move))

(defn get-cabin-move
  [state]
  (:cabin-current-move state))

(defn cabin-dont-move
  [state]
  (dissoc state :cabin-current-move))

(defn wipe-cabin-commands
  [state]
  (update state :cabin elevator-cabin/commands-processed))

(defn get-cabin-commands
  [state]
  (elevator-cabin/get-commands (:cabin state)))

(defn emit-cabin-command
  [state command]
  (update state :cabin #(elevator-cabin/add-command % command)))

;; workers stuffs
;; --------------

(defn get-workers
  [state]
  (vals (:workers (:workers state))))

(defn get-worker-sprite
  [state worker-id]
  (get-in state [:objects :workers worker-id :sprite]))

(defn get-worker-move
  [state worker-id]
  (get-in state [:objects :workers worker-id :current-move]))

(defn get-worker-tween
  [state worker-id]
  (get-in state [:objects :workers worker-id :tween]))

(defn set-worker-sprite
  [state worker-id sprite]
  (assoc-in state [:objects :workers worker-id :sprite] sprite))

(defn set-worker-move
  [state worker-id move]
  (assoc-in state [:objects :workers worker-id :current-move] move))

(defn set-worker-tween
  [state worker-id tween]
  (assoc-in state [:objects :workers worker-id :tween] tween))

(defn worker-dont-move
  [state worker-id]
  (update-in state [:objects :workers worker-id] dissoc :tween :current-move))

(defn get-workers-commands
  [state]
  (workers/get-commands (:workers state)))

(defn wipe-workers-commands
  [state]
  (update state :workers workers/commands-processed))

(defn emit-worker-command
  [state command]
  (update state :workers #(workers/add-command % command)))