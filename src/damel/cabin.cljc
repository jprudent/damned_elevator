(ns damel.cabin
  (:require [damel.state :as state]
            [damel.phaser-utils :as phutils]
            [damel.game-dimensions :as game-dimensions]
            [damel.kinetic :as kinetic]))

;; -- getters

(defn get-cabin-obj-coors
  [state]
  (-> state
      (state/get-cabin-sprite)
      (phutils/get-obj-coors)))

;; -- cabin manipulation

(defrecord CabinMove [destination-y upward?])

(defn go-to-level
  "do graphics manipulations and returns updated state"
  [state game level]
  (let [cabin   (state/get-cabin-sprite state)
        {:keys [x y]} (game-dimensions/cabin-at-level level)
        {current-y :y} (get-cabin-obj-coors state)
        upward? (neg? (- y current-y))]
    (do (phutils/move-to game cabin x y)
        (state/set-cabin-move state (->CabinMove y upward?)))))

(defn stop-cabin
  "do graphics manipulations and returns updated state"
  [state game]
  (let [cabin (state/get-cabin-sprite state)]
    (do (phutils/set-velocity cabin 0)
        (state/cabin-dont-move state))))

;; -- cabin commands handling

(defn cabin-command-handler
  "do graphics manipulations and returns updated state"
  [state game [type params]]
  (case type
    :command/go-to-level
    (go-to-level state game (:level params))))

(defn- play-all-commands
  "do graphics manipulations and returns updated state"
  [state game]
  (reduce
    (fn [state command] (cabin-command-handler state game command))
    state
    (state/get-cabin-commands state)))

(defn process-commands
  "do graphics manipulations and returns updated state"
  [state game]
  (-> state
      (play-all-commands game)
      (state/wipe-cabin-commands)))

(defn control-move
  "do graphics manipulations and returns updated state"
  [state game]
  (let [move (state/get-cabin-move state)
        sprite-coors (get-cabin-obj-coors state)]
    (if (and move (kinetic/arrived-at-destination-y? move sprite-coors))
      (-> (stop-cabin state game)
          (state/emit-cabin-command [:command/cabin-arrived]))
      state)))