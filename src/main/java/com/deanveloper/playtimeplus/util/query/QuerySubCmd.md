# Queries
Queries are tools that allow you to filter out data that you do not want.

## Usage
The formal syntax for a query is `"<type:time> [<operation> <type:time>]..."`

## Arguments
As you see, there are 3 different things that make up queries. Types, Times, and Operations. Let's dive right in and talk about what each one of these mean.

### Types
Query types represent the type of query that is going to be performed. There are 3 types of queries: `before`, `after`, and `total`. Lets go over these.

`Before` filters out any time that isn't before the time specified.

`After` filters out any time that isn't after the time specified.

`Total` will filter out all players who don't have more or less total time than how much is specified.
Note that you must use a `Duration` for this, you cannot enter a `DateTime`. Let's get into what that means.

### Times
There are two ways to show time.

The first way is to use a specific `DateTime`. You can do this by following the format `[Y:####][M:##][d:##][h:##][m:##][s:##]`.
Any of these are optional, and if you decide not to specify any of these time units, it will default to whatever time unit we are currently at.
(For instance, if it is 2016 and you don't specify a year, it will assume that you want the year parameter to be 2016.)

The second way is to use a `Duration`. The format for these are `####Y##M##d##h##m##s`.
These must be used if you are going to query by `total`, although you can use them for `after` and `before` as well.
If you use a `Duration` for the `after` or `before` types, it will convert the `Duration` to a `DateTime` by subtracting the `Duration` from the current `DateTime`.
For instance, if you say `after:7d` and it was `Y:2016,M:9,d:19,h:5,m:0,s:0`, the filter would apply to times after `Y:2016,M:9,d:12,h:5,m:0,s:0`.
This might seem complicated, but there's only one hard-to-understand part before the examples when this will all be more clear.

### Operators
Maybe being limited to `before`, `after`, and `total` just doesn't cut it. You need something more specific. This is when operators come to use.
Operators allow you to combine queries together to make new ones. There are two operators to use: `or` and `and`.
Let's go over what these mean.

`or` means "one, or the other, or both".
So if you perform the query `before:7d or after:4d` it will result in all players' play times which are either before 7 days ago or after 4 days ago.

`and` means "both one and the other".
So if you perform `after:7d and before:4d`, it will result in all players' play times which are both after 7 days ago and before 4 days ago.
This is the same as saying "in between 7 and 4 days ago"

## Examples
Finally! Now you can figure out what all that crap means.

Let's say you wanted to find all players who have played in the past week. This is a fairly simple query. `after:7d`.
This query within the code translates to "show me each player's play times that are after 7 days ago".

So if we want to find all active players, we'll need more than just "logged on in the past week" to define active. We'll assume you had to have played 2 hours in the past week to do this.
This can also be pretty simply done in one command. Let's use those operators that we mentioned earlier.
If we use the `and` operator, we can make sure that we get play times of players who have both played in the past week AND have at least 2 hours since then.
The proper query to do this would be `after:7d and total>2h`. Note that total uses `>` or `<` rather than a `:`, as we want to specify that the total should be greater than 2 hours.
Internally, it will read this query as "show me each player's play time in the past 7 days, only if that amount of play time is over 2 hours"

Let's say we want to find inactive players. To define an inactive player, let's say that they need to have been on for at least a month and have less than 6 hours of play time.
We'll use the `and` operator for this, as you might guess.
Although there's a slight problem. If you use `before:1M and total<5h`, it will only get the total hours **before one month ago.**
This means that if someone joined 1 month and a day ago, and only played 3 hours that day, but has played 20 since then, they'll get marked for inactivity.
To combat this, put the total first. The proper syntax is `total<5h and before:1M`, as this would first filter out play times of people who have less than 5 hours, and then apply to that the `before:1M`.

If this seems super confusing, feel free to contact me and I'll try to explain further.