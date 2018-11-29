(ns damel.physics)


;; if game run at 60fps dt will be (/ 1000 60) = 16.6 ms
;; I think this is Euler integration :
(defn move [{:keys [force
                    speed
                    mass
                    position]
             :as state}
            dt]
  (let [new-acceleration (/ force mass)
        new-speed (+ speed (* new-acceleration dt))
        new-position (+ position (* new-speed dt))]
    (assoc state
      :speed new-speed
      :position new-position
      :acceleration new-acceleration)))