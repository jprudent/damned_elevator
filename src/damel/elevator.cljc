(ns damel.elevator)

(def elevator-0 {:z-position 0
                 ;; z per tick
                 :speed 0})
(def world-0
  {:tick     0
   :elevator elevator-0
   :levels 4})

(defn move
  "move elevator where it would be in nb-ticks"
  [{:keys [speed] :as elevator} nb-ticks]
  (update elevator :z-position + (* nb-ticks speed)))

(defn set-speed
  "set the speed of the elevator"
  [elevator speed]
  (assoc elevator :speed speed))

(defn )

(def state (atom world-0))

