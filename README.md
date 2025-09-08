# Faulty Tower Elevator

An elevator that Faulty Towers would be proud of.

## Design notes

Consider:
- set of floors
- call from floor in direction
- button press to floor
- bank of elevators
- secure floors
- express and maintenance
- emergency (fire)
- default or set positions

Efficiency is likely very dependent on the above, plus:
  - building layout (e.g. residential and commercial w/ 
    different load shapes)
  - time of day
  - special events
  - likely more

Take away is that most efficient, is likely context dependant.
Next best is likely the LOOK algorithm - and feels like most
typical elevator movements.

## Bank of Elevators

- Centralized
  - Simpler to implement extended orchestration
  - SPOF
- Decentralized
  - Strong consistency likely not an issue - if 2 elevators respond
    to same call, not really a big deal.  Gossip > Raft.

### Out of scope

- Physical control pieces:
  - emergency brakes
  - inertia, speed, etc
  - Doors
  - People and crowding (SimTower already did it best)
  - Earthquakes / tornadoes / Godzilla / Xenomorphs
- Network partitioning, jitter, retry, etc
- Internet requests (e.g. security from mobile phone requests)
- 