(ns damel.elevator
  (:require [damel.physics :as physics]))

(def elevator-0 {:position     0
                 :speed        0
                 :acceleration 0
                 :force        0
                 :mass         1})
(def world-0
  {:t               0
   :elevator        elevator-0
   :levels          4
   :external-events []})

(defn add-event [world event]
  (update world :external-events conj event))

(defrecord MotorPulled [force])
(defrecord MotorStopped [])

(defprotocol EventHandler
  (apply-event [this world]))

(defn- set-force [world force] (assoc-in world [:elevator :force] force))

(extend-protocol EventHandler
  MotorPulled
  (apply-event [{:keys [force]} world] (set-force world force))

  MotorStopped
  (apply-event [_ world] (set-force world 0)))

(defn- apply-external-events [{:keys [external-events] :as world}]
  (-> (reduce #(apply-event %2 %1) world external-events)
      (assoc :external-events [])))

(defn game-loop
  "move the world as it should be in the next frame"
  [world dt]
  (-> (apply-external-events world)
      (update :elevator physics/move dt)))

(def state (atom world-0))

