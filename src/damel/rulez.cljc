(ns damel.rulez
  (:require [clara.rules.engine :as eng]
            [clara.rules.accumulators :as acc]
            [clara.rules :as clara]))

(clara/defsession example-session 'damel.workers)



(defrecord Elevator [weight
                     max-weight
                     nb-workers
                     current-level
                     heading-level])


(defrule
  total-purchases
  [?total <- (acc/sum :cost) :from [Purchase]]
  =>
  (insert! (->Total ?total)))