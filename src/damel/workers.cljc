(ns damel.workers
  (:require [damel.state :as state]
            [damel.game-objects-factory :as gof]
            [damel.game-dimensions :as game-dimensions]
            [damel.kinetic :as kinetic]
            [damel.phaser-utils :as phutils]))

(defn spawn-worker
  [state game [_ {:keys [id] :as worker}]]
  (state/set-worker-sprite state id (gof/make-worker game worker)))

(defrecord WorkerMove [destination-x to-left?])

(defn go-to-elevator
  [state game [_ {:keys [id] :as worker}]]
  (let [destination-x (:x-center game-dimensions/cabin)
        worker-sprite (state/get-worker-sprite state id)
        worker-tween  (gof/make-walking-worker-tween game worker-sprite destination-x)
        worker-move   (->WorkerMove destination-x (pos? (- (.. worker-sprite -x) destination-x)))]
    (-> state
        (state/set-worker-move id worker-move)
        (state/set-worker-tween id worker-tween))))

(defn worker-command-handler
  [state game [command-type :as command]]
  (case command-type
    :added-worker (spawn-worker state game command)
    :ordered-to-elevator (go-to-elevator state game command)))

(defn- play-commands [state game]
  (reduce
    (fn [state command] (worker-command-handler state game command))
    state
    (state/get-workers-commands state)))

(defn process-commands
  [state game]
  (-> state
      (play-commands game)
      (state/wipe-workers-commands)))

(defn control-worker-move
  [state worker-id]
  (let [move   (state/get-worker-move state worker-id)
        sprite (state/get-worker-sprite state worker-id)
        tween  (state/get-worker-tween state worker-id)]
    (if (and move (kinetic/arrived-at-destination-x? move (phutils/get-obj-coors sprite)))
      (do (.stop tween)
          (state/worker-dont-move state worker-id))
      state)))

(defn control-move
  [state game]
  (reduce
    (fn [state worker-id] (control-worker-move state worker-id))
    state
    (map :id (state/get-workers state))))