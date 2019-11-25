(ns bs.events
  (:require [bs.db :as db]
            [bs.state :as state]))

(defmethod state/reg-event :event/start [_ _]
  (db/make))

(defmethod state/reg-event :event/start-dragging [db [_ pos]]
  (->
   (db/start-dragging db pos)
   (db/hover-cell pos)))

(defmethod state/reg-event :event/stop-dragging [db _]
  (db/stop-dragging db))

(defmethod state/reg-event :event/hover-cell [db [_ pos]]
  (db/hover-cell db pos))
