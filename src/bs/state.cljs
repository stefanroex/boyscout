(ns bs.state
  (:require [reagent.core :as r]))

(defonce app-db (r/atom nil))

(defmulti reg-event (fn [_ [event-id]] event-id))

(defmethod reg-event :default [db [event-id]]
  (.warn js/console (str "Event " event-id " is not implemented."))
  db)

(defn dispatch [event]
  (swap! app-db reg-event event)
  true)
