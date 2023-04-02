(ns shadow-boilerplate.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async :refer [<! >! put! chan]]
            ["nostr-tools" :as nostr]))

(def sk (nostr/generatePrivateKey)

(def pub-key
  (nostr/getPublicKey sk))

(defn make-event [msg]
  (let [event {:kind 1
               :created_at (.floor js/Math (/ (.now js/Date) 1000))
               :tags []
               :content msg
               :pubkey pub-key}
        event (assoc event :id (nostr/getEventHash (clj->js event)))]
    (assoc event :sig (nostr/signEvent (clj->js event) sk ))))

(defn valid-fn [event]
  (nostr/validateEvent (clj->js event))) ;; true

(defn verified-fn [event]
  (nostr/verifySignature (clj->js event)));;true

(defn relay-connect
  [^js/Relay relay]
  (let [c (chan)]
   (.on relay "connect" (fn []
                          (js/console.log "Connected to " (.-url relay))
                          (put! c [nil true])))
   (.on relay "error" (fn []
                        (put! c [:error])))
   (.connect relay)
   c))

(defn publish-event
  [^js/Relay relay event]
  (let [c (chan)
        pub (.publish relay (clj->js event))]
    (.on pub "ok" (fn [_]
                    (js/console.log "published event: " (clj->js event) )
                    (put! c [nil true])))
    (.on pub "failed"
         (fn [reason]
           (put! c [reason])))
    c))

;;https://nostr.watch
(def relay (nostr/relayInit "wss://relay.damus.io"))


(defn dispatch-message
  [msg]
  (let [event-signed (make-event msg)
        _ (when-not (or (valid-fn event-signed) (verified-fn event-signed))
            (throw (js/Error. "Event not valid/verified")))]
    (go
      (let
          [[err _] (<! (relay-connect relay))
           _ (when err (throw (js/Error. (str "Can't connect to: " (.-url relay)))))

           [err _] (<! (publish-event relay event-signed))
           _ (when err (throw (js/Error. (str "Can't publish due to: " err))))]
        (set! js/window.location.href (str "https://www.nostr.guru/e/" (:id event-signed)))))))

(defn init [& args]
  (let [input (js/document.createElement "input")]
    (.addEventListener input "keydown"
                       (fn [event]
                         (js/console.log event)
                         (when (= (.-key event) "Enter")
                           (.preventDefault event)
                           (dispatch-message (.-value input)))))
    (.appendChild js/document.body input))
  )
