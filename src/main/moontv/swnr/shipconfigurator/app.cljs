(ns moontv.swnr.shipconfigurator.app
  (:require
   [clojure.string]
   [moontv.swnr.shipconfigurator.sub :as sub] ; must be imported so it isn't lost in treeshaking
   [moontv.swnr.shipconfigurator.views :as views]
   [re-frame.core :as rf]
   [moontv.swnr.shipconfigurator.events :as events]
   [reagent.dom.client :as rdc]))

;;(defn dispatch-timer-event
;;  []
;;  (let [now (js/Date.)]
;;    (rf/dispatch [:timer now])))  ;; <-- dispatch used

;; Call the dispatching function every second.
;; `defonce` is like `def` but it ensures only one instance is ever
;; created in the face of figwheel hot-reloading of this file.
;(defonce do-timer (js/setInterval dispatch-timer-event 1000))

;; -- Domino 2 - Event Handlers -----------------------------------------------
;; -- Entry Point -------------------------------------------------------------

(defonce root-container
  (rdc/create-root (js/document.getElementById "root")))

(defn mount-ui
  []
  (rdc/render root-container [views/ui]))

(defn ^:dev/after-load clear-cache-and-render!
  []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (rf/clear-subscription-cache!)
  (mount-ui))

(defn init               ;; Your app calls this when it starts. See shadow-cljs.edn :init-fn.
  []
  (rf/dispatch-sync [::events/initialize]) ;; put a value into application state
  (mount-ui))                      ;; mount the application's ui into '<div id="app" />'
