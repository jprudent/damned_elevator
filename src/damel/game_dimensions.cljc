(ns damel.game-dimensions)

(def floor-height 10)
(def floor-width 600)
(def nb-levels 10)
(def level-height 200)
(def world-height (* nb-levels level-height))
(def level-text-size 14)
(def elevator-height 166)
(def elevator-width 225)


(defn level-dimensions
  [level-number]
  (let [floor-y-min (- world-height (* level-number level-height))
        floor-y     (- floor-y-min floor-height)]
    {:floor    {:x     0
                :y floor-y
                :width floor-width
                :height floor-height}
     :elevator {:x 0
                :y (- floor-y elevator-height)
                :width elevator-width
                :height elevator-height}}))