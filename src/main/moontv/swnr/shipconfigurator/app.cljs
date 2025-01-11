(ns moontv.swnr.shipconfigurator.app
  (:require
   [clojure.string]
   [re-frame.core :as rf]
   [reagent.dom.client :as rdc]))

;; A detailed walk-through of this source code is provided in the docs:
;; https://day8.github.io/re-frame/dominoes-live/

(comment #_{:clj-kondo/ignore [:duplicate-require]}
 (require '[re-frame.db :as db])
         @db/app-db)
;; -- Domino 1 - Event Dispatch -----------------------------------------------

(defn dispatch-timer-event
  []
  (let [now (js/Date.)]
    (rf/dispatch [:timer now])))  ;; <-- dispatch used

;; Call the dispatching function every second.
;; `defonce` is like `def` but it ensures only one instance is ever
;; created in the face of figwheel hot-reloading of this file.
(defonce do-timer (js/setInterval dispatch-timer-event 1000))

;; -- Domino 2 - Event Handlers -----------------------------------------------

(rf/reg-event-db              ;; sets up initial application state
 :initialize                 ;; usage:  (dispatch [:initialize])
 (fn [_ _]                   ;; the two parameters are not important here, so use _
   (let [ship-data (->> {:strike-fighter ["Strike Fighter" 200000 5 5 8 1 1 16 5 2 1 :fighter]
                         :shuttle ["Shuttle" 200000 3 0 15 1 10 11 3 5 1 :fighter]
                         :free-merchant ["Free Merchant" 500000 3 2 20 1 6 14 10 15 2 :frigate]
                         :patrol-boat ["Patrol Boat" 2500000 4 5 25 5 20 14 15 10 4 :frigate]
                         :corvette ["Corvette" 4000000 2 10 40 10 40 13 15 15 6 :frigate]
                         :heavy-frigate ["Heavy Frigate" 7000000 1 10 50 30 120 15 25 20 8 :frigate]
                         :bulk-freighter ["Bulk Freighter" 5000000 0 0 40 10 40 11 15 25 2 :cruiser]
                         :fleet-cruiser ["Fleet Cruiser" 10000000 1 15 60 50 200 14 50 30 10 :cruiser]
                         :battleship ["Battleship" 50000000 0 20 100 200 1000 16 75 50 15 :capital]
                         :small-station ["Small Station" 5000000 nil 5 120 20 200 11 50 40 10 :cruiser]
                         :large-station ["Large Station" 40000000 nil 20 120 100 1000 17 125 75 30 :capital]}
                        (map (fn [[key value]]
                               [key
                                (zipmap [:name :cost :speed :armor :hp :crew-min :crew-max :ac :power :mass :hardpoints :class]
                                        value)]))
                        (into {}))] ;; TODO: fix ordering in UI (maybe i don't even want a map and instead want a [[] [] []...])
     {:time (js/Date.)         ;; What it returns becomes the new application state
      :time-color "orange"
      :ship-data ship-data
      :selected-ship :large-station})))  ;; so the application state will initially be a map with two keys

(rf/reg-event-db                ;; usage:  (dispatch [:time-color-change 34562])
 :time-color-change            ;; dispatched when the user enters a new colour into the UI text field
 (fn [db [_ new-color-value]]  ;; -db event handlers given 2 parameters:  current application state and event (a vector)
   (assoc db :time-color new-color-value)))   ;; compute and return the new application state

(rf/reg-event-db                 ;; usage:  (dispatch [:timer a-js-Date])
 :timer                         ;; every second an event of this kind will be dispatched
 (fn [db [_ new-time]]          ;; note how the 2nd parameter is destructured to obtain the data value
   (assoc db :time new-time)))  ;; compute and return the new application state

(rf/reg-event-db
 :select-ship
 (fn [db [_event-name type]]
   (when (not (identical? (:selected-ship db) type))
     (when (js/confirm "Changing your hull type will delete your data, are you sure?") ;; REVIEW: is this impurity okay?
       (assoc db :selected-ship type)))))

;; -- Domino 4 - Query  -------------------------------------------------------

(rf/reg-sub
 :time
 (fn [db _]     ;; db is current app state. 2nd unused param is query vector
   (:time db))) ;; return a query computation over the application state

(rf/reg-sub
 :time-color
 (fn [db _]
   (:time-color db)))

(rf/reg-sub
 :ship-data
 (fn [db _]
   (:ship-data db)))

(rf/reg-sub
 :selected-ship
 (fn [db _]
   (:selected-ship db)))

;; -- Domino 5 - View Functions ----------------------------------------------

(defn clock
  []
  (let [colour @(rf/subscribe [:time-color])
        time   (-> @(rf/subscribe [:time])
                   .toTimeString
                   (clojure.string/split " ")
                   first)]
    [:div.example-clock {:style {:color colour :font-size "48px"}} time]))

(defn color-input
  []
  (let [gettext (fn [e] (-> e .-target .-value))
        emit    (fn [e] (rf/dispatch [:time-color-change (gettext e)]))]
    [:div.color-input
     "Display color: "
     [:input {:type "text"
              :style {:border "1px solid #CCC"}
              :value @(rf/subscribe [:time-color])        ;; subscribe
              :on-change emit}]]))  ;; <---

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
