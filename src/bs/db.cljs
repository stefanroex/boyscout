(ns bs.db
  (:require [bs.board :as board]))

;; Private

(defn- drag-mode [board pos]
  (cond
    (board/target? board pos) :drag/target
    (board/source? board pos) :drag/source
    (board/wall? board pos) :drag/destroy-wall
    :else :drag/make-wall))

;; Public

(defn make []
  {:db/board (-> (board/make 26 40)
                 (board/set-source [1 1])
                 (board/set-target [4 4]))})

(defn start-dragging [{:db/keys [board] :as db} pos]
  (assoc db :db/drag-mode (drag-mode board pos)))

(defn stop-dragging [db]
  (dissoc db :db/drag-mode))

(defn hover-cell [{:db/keys [drag-mode] :as db} pos]
  (case drag-mode
    :drag/make-wall (update db :db/board board/make-wall pos)
    :drag/destroy-wall (update db :db/board board/destroy-wall pos)
    :drag/source (update db :db/board board/set-source pos)
    :drag/target (update db :db/board board/set-target pos)
    db))
