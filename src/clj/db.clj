(ns db
  (:require [next.jdbc :as jdbc]
            [clojure.string :as s]))

(def db-spec {:jdbcUrl   "jdbc:sqlite:data/database.db"})

(def schema-user-table ["create table if not exists users (
    id integer primary key,
    first_name text not null
)"])

(def insert-initial-users-statement ["insert into users (first_name) values
  ('Jerry'),
  ('Jenny'),
  ('George'),
  ('Johanna'),
  ('John'),
  ('Anne')"])

(def add-token-column-migration ["ALTER TABLE users ADD token text"])

(defn run-add-token-migration []
  (jdbc/execute! db-spec add-token-column-migration))

(defn create-table [table-schema db-spec]
  (try
    (jdbc/execute! db-spec  table-schema)
    (catch Exception e
      (println (.getMessage e)))))


(defn seed-db []
  (with-open [connection (jdbc/get-connection db-spec)]
    (jdbc/execute! connection  insert-initial-users-statement)))

(defn snake-case->kebab-case
  [column]
  (when (keyword? column)
    (keyword (s/replace (name column) #"_" "-"))))

(defn format-output-keywords
  "Convert `output` keywords from snake_case to kebab-case."
  [output]
  (reduce-kv (fn [m k v]
               (assoc m (snake-case->kebab-case k) v))
             {}
             output))

(defn ^:dynamic fetch-user [id]
  (with-open
   [connection (jdbc/get-connection db-spec)]
    (->>
     (let [result (jdbc/execute-one! connection  ["select * from users where id= ? " id])]
       (if (nil? result) nil
           (format-output-keywords result))))))

(defn ^:dynamic search-user [search]
  (if (s/blank? search) nil
      (with-open
       [connection (jdbc/get-connection db-spec)]
        (->>
         (let [result (jdbc/execute-one! connection  ["select * from users where first_name like ? limit 1", (str "%" search "%")])]
           (if (nil? result) nil
               (format-output-keywords result)))))))

(defn ^:dynamic get-user-by-token [token]
  (with-open
   [connection (jdbc/get-connection db-spec)]
    (->>
     (let [result (jdbc/execute-one! connection  ["select * from users where token = ?", token])]
       (if (nil? result) nil
           (format-output-keywords result))))))

(defn fetch-user-count []
  (with-open
   [connection (jdbc/get-connection db-spec)]
    (->>
     (let [result (jdbc/execute-one! connection  ["select count(*) from users"])]
       (-> result
           (first)
           (second))))))

(defn add-tokens-to-users []
  (with-open
   [connection (jdbc/get-connection db-spec)]
    (let [user-count (fetch-user-count)
          user-ids (range 1 (inc user-count))]
      (doseq [id user-ids]
        (let [token (.toString (java.util.UUID/randomUUID))]
          (jdbc/execute! connection ["update users set token = ? where id = ?", token, id]))))))


(comment
  (create-table schema-user-table db-spec)
  (seed-db)
  (run-add-token-migration)
  (add-tokens-to-users))