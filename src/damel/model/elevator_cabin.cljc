(ns damel.model.elevator-cabin)

(def cabin-0 {:current-level 0
              :heading-level 0
              :mass          1000
              :commands      []})

(defn handle-go-to-level
  [{:keys [current-level] :as cabin}
   [_ {:keys [level]} :as command]]
  (when (not= current-level level)
    (-> (assoc cabin :heading-level level)
        (update :commands conj command))))

(defn handle-cabin-arrived
  [{:keys [heading-level] :as cabin} command_]
  (-> (assoc cabin :current-level heading-level)
      (dissoc :heading-level)))

(defn add-command
  [cabin [command-type :as command]]
  (case command-type
    :command/go-to-level (handle-go-to-level cabin command)
    :command/cabin-arrived (handle-cabin-arrived cabin command)))

(defn commands-processed
  [cabin]
  (update cabin :commands empty))

(def get-commands :commands)
