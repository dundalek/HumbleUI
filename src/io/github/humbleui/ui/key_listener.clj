(ns io.github.humbleui.ui.key-listener
  (:require
    [io.github.humbleui.core :as core]
    [io.github.humbleui.paint :as paint]
    [io.github.humbleui.protocols :as protocols]
    [io.github.humbleui.ui.clickable :as clickable])
  (:import
    [java.lang AutoCloseable]))

(core/deftype+ KeyListener [on-key-down on-key-up child ^:mut child-rect]
  protocols/IComponent
  (-measure [_ ctx cs]
    (core/measure child ctx cs))
  
  (-draw [_ ctx rect ^Canvas canvas]
    (set! child-rect rect)
    (core/draw-child child ctx rect canvas))
  
  (-event [_ ctx event]
    (core/eager-or
      (when (= :key (:event event))
        (if (:pressed? event)
          (when on-key-down
            (on-key-down event))
          (when on-key-up
            (on-key-up event))))
      (core/event-child child ctx event)))
  
  (-iterate [this ctx cb]
    (or
      (cb this)
      (protocols/-iterate child ctx cb)))
  
  AutoCloseable
  (close [_]
    (core/child-close child)))

(defn key-listener [{:keys [on-key-down on-key-up]} child]
  (->KeyListener on-key-down on-key-up child nil))