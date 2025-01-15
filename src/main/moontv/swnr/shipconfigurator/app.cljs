(ns moontv.swnr.shipconfigurator.app
  (:require
   [clojure.string]
   [re-frame.core :as rf]
   ;; TODO import events file
   ;; TODO import subs file
   [moontv.swnr.shipconfigurator.db-spec :as db-s]
   [reagent.dom.client :as rdc]))

;; A detailed walk-through of this source code is provided in the docs:
;; https://day8.github.io/re-frame/dominoes-live/

(comment #_{:clj-kondo/ignore [:duplicate-require]}
 (require '[re-frame.db :as db])
         @db/app-db)
;; -- Domino 1 - Event Dispatch -----------------------------------------------

;;(defn dispatch-timer-event
;;  []
;;  (let [now (js/Date.)]
;;    (rf/dispatch [:timer now])))  ;; <-- dispatch used

;; Call the dispatching function every second.
;; `defonce` is like `def` but it ensures only one instance is ever
;; created in the face of figwheel hot-reloading of this file.
;(defonce do-timer (js/setInterval dispatch-timer-event 1000))

;; -- Domino 2 - Event Handlers -----------------------------------------------

(rf/reg-event-db              ;; sets up initial application state
 :initialize                 ;; usage:  (dispatch [:initialize])
 (fn [_ _]                   ;; the two parameters are not important here, so use _
   (let [ship-data (->> {:strike-fighter ["Strike Fighter" 200000 5 5 8 1 1 16 5 2 1 ::db-s/fighter]
                         :shuttle ["Shuttle" 200000 3 0 15 1 10 11 3 5 1 ::db-s/fighter]
                         :free-merchant ["Free Merchant" 500000 3 2 20 1 6 14 10 15 2 ::db-s/frigate]
                         :patrol-boat ["Patrol Boat" 2500000 4 5 25 5 20 14 15 10 4 ::db-s/frigate]
                         :corvette ["Corvette" 4000000 2 10 40 10 40 13 15 15 6 ::db-s/frigate]
                         :heavy-frigate ["Heavy Frigate" 7000000 1 10 50 30 120 15 25 20 8 ::db-s/frigate]
                         :bulk-freighter ["Bulk Freighter" 5000000 0 0 40 10 40 11 15 25 2 ::db-s/cruiser]
                         :fleet-cruiser ["Fleet Cruiser" 10000000 1 15 60 50 200 14 50 30 10 ::db-s/cruiser]
                         :battleship ["Battleship" 50000000 0 20 100 200 1000 16 75 50 15 ::db-s/capital]
                         :small-station ["Small Station" 5000000 nil 5 120 20 200 11 50 40 10 ::db-s/cruiser]
                         :large-station ["Large Station" 40000000 nil 20 120 100 1000 17 125 75 30 ::db-s/capital]}
                        (map (fn [[key value]]
                               [key
                                (zipmap [::db-s/ship-name ::db-s/ship-cost ::db-s/ship-speed ::db-s/ship-armor
                                         ::db-s/ship-hp ::db-s/ship-crew-min ::db-s/ship-crew-max ::db-s/ship-ac
                                         ::db-s/ship-power ::db-s/ship-mass ::db-s/ship-hardpoints ::db-s/ship-class]
                                        value)]))
                        (into {}))] ;; TODO: fix ordering in UI (maybe i don't even want a map and instead want a [[] [] []...])
     {::db-s/ship-data ship-data
      ::db-s/selected-ship :large-station})))  ;; so the application state will initially be a map with two keys

(rf/reg-event-db
 :select-ship
 (fn [db [_event-name type]]
   (when (not (= (:selected-ship db) type))
     (when (js/confirm "Changing your hull type will delete your data, are you sure?") ;; REVIEW: is this impurity okay?
       ;; TODO: implement undo / redo so we no longer need that check
       (assoc db :selected-ship type)))))

;; -- Domino 4 - Query  -------------------------------------------------------

(rf/reg-sub
 :ship-data
 (fn [db _]
   (:ship-data db)))

(rf/reg-sub
 :selected-ship
 (fn [db _]
   (:selected-ship db)))

;; -- Domino 5 - View Functions ----------------------------------------------

(defn hull-selector-line [[ship-type ship-data] on-mouse-down]
  ^{:key ship-type}
  [:tr {:on-mouse-down on-mouse-down}
   (for [column [:name :cost :speed :armor :hp :crew-min :crew-max :ac :power :mass :hardpoints :class]]
     ^{:key column} [:td (get ship-data column)])])

(defn hull-selector
  []
  (let [ships @(rf/subscribe [:ship-data])
        select-ship (fn [type] (rf/dispatch [:select-ship type]))]
    [:details
     {:open true}
     [:table
      [:thead [:tr (->> '("Hull Type" "Cost" "Speed" "Armor" "HP" "Crew-Min" "Crew-Max" "AC" "Power" "Mass" "Hardpoints" "Class")
                        (map (fn [column-name] ^{:key column-name} [:th column-name])))]]
      [:tbody
       (for [[ship-type ship] ships]
         ^{:key ship-type} [hull-selector-line [ship-type ship] #(select-ship ship-type)])]]]))

(defn fitting-selector
  []
  [:div "this is the fitting selector"])

(defn defenses-selector
  []
  [:div "this is the defenses selector"])

(defn weaponry-selector
  []
  [:div "this is the weaponry selector"])

(defn starship-summary
  []
  (let [selected-ship @(rf/subscribe [:selected-ship])]
    [:div "this is the starship summary"
     [:table
      [:thead>tr>th (:name (selected-ship @(rf/subscribe [:ship-data])))]]]))

(defn swnr-starship-configurator-app
  []
  [:div
   [:h1 "SWNr Starship Configurator"]
   [hull-selector]
   [fitting-selector]
   [defenses-selector]
   [weaponry-selector]
   [starship-summary]])

(defn ui
  []
  [:div
   [:h1 "ze website"]
   [swnr-starship-configurator-app]])

;; -- Entry Point -------------------------------------------------------------

(defonce root-container
  (rdc/create-root (js/document.getElementById "root")))

(defn mount-ui
  []
  (rdc/render root-container [ui]))

(defn ^:dev/after-load clear-cache-and-render!
  []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (rf/clear-subscription-cache!)
  (mount-ui))

(defn init               ;; Your app calls this when it starts. See shadow-cljs.edn :init-fn.
  []
  (rf/dispatch-sync [:initialize]) ;; put a value into application state
  (mount-ui))                      ;; mount the application's ui into '<div id="app" />'
