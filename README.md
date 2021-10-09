# Clojure Interview Project

The aim of this project is to verify that you're able to produce usable, legible
Clojure/ClojureScript. We will be looking at the quality of the code itself as
well as of the testing / validation that is done.

The whole thing is broken down into 3 tasks should only take a couple of hours.
If you are confused or have any questions, [email
me](mailto:matthewrea.c@gmail.com).

## Setup

This project is set up with `lein`, and a simple `lein repl` should get you a
functional clojure repl. `lein watch-cljs` will start a cljs watcher that builds
the frontend code (which can also be connected to via nREPL).

You will also need `sqlite3` during task 2 (and will need to add appropriate
dependencies to `project.clj` depending on what db library you want to use to
interact with it).

### Verifying Setup

Use `lein watch-cljs` to start the watcher, then run `(pingpong.core/start!)` in
a CLJ repl to start the development server.
[localhost:9001](http://localhost:9001/) will then display a very basic form. If
you click the "Ping!" button, the server should log a request to `/api/ping` and
the browser should display a "pong" message.

Verify that this is functioning before beginning.

## Testing

`lein test` will run the Clojure tests. Do not mock the database---but use a
transaction if you do any modifications during testing.

Do not worry about ClojureScript tests for this project. We have an internal
setup to run CLJS tests, and many CLJS test setups don't play nicely with
Shadow-CLJS (while Shadow-CLJS _also_ effectively requires using Karma directly
to run CLJS tests, and I don't want complicate the setup to do that).

## Tasks

Please read each task fully before beginning, as it will help you avoid misteps
and save time. Each task must be committed separately and the completed version
tagged in git (a tag is given at the bottom of each task)

### Task 1

1. Add an `<input>` to the form and send its contents to `/api/ping` endpoint
   when "Ping!" is clicked. How exactly this is represented is up to you.
2. When the "Ping!" endpoint receives a `value`, it should respond with
   `{"ping": value}`. If no `value` is received or it is blank, it should
   continue responding with `{"ping": "pong"}`.

When complete, commit your work and tag it `task-1` in git.

### Task 2

1. Create an SQLite database from `data/init.sql`. This has a single table: `users`.
2. When the first ping is received after server start, retrieve the user with
   `id = 1` and send its `first_name` on the `"name"` key in the response object (e.g.
   `{"ping": "pong", "name": "George"}`). On the 2nd ping, respond with `id = 2`,
   etc. If there are no more users, then it should reset to the top of the
   list. The counter should **not** persist between server starts.
3. Add a description list to the frontend form to show the value of the `ping`
   and `name` keys on the response.

When complete, commit your work and tag it `task-2` in git.

### Task 3

1. Replace the code for user loading based on the counter with code that
   loads a patient based on a substring match (e.g. `org` matches `George`). If
   no match is found, do not return the `name` key on the JSON response (it
   should NOT be null! `{"ping": "pong"}` is correct,
   `{"ping": "pong", "name": null}` is not). If there are multiple matches,
   pick the one that is lexicographically first (so "Anne" is before "Johanna").
2. Generate a UUID token and return it in place of the `name`. Add an endpoint
   `/api/token` that retrieves the actual name based on this token. The tokens
   should be persistent. (Don't worry about using a dedicated UUID type in the
   database---SQLite requires an extension for this that you may not have).
3. Modify the frontend to retrieve the name using the token.

When complete, commit your work and tag it `task-3` in git.

## Completion

When the tasks are finished, [email me](mailto:matthewrea.c@gmail.com) to let me
know, along with a brief summary of:

1. the relative difficulty of each task (easiest/hardest/middle).
2. any comments you have about the task or difficulties (especially with setup)
   that you encountered.
