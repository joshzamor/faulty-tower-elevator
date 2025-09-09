# Faulty Tower Elevator

A toy elevator that Fawlty Towers would be proud of.

## Requirements

- Make (optional)
- Docker
  - Alternatively:  JDK 21+ and Maven 3.9+

## Quick Start

1. Build: `make`
2. Run tests: `make test`

Or without Make:

1. Build: `docker build jz/faulty-tower-elevator:latest .`
2. Run tests: `docker run jz/faulty-tower-elevator:latest`

Or without Docker:

1. Build: `mvn compile`
2. Run tests: `mvn test`

## Overview

This simulates a bank/group of elevators that are able to coordinate serving requests
for floor destinations.

Features:

- A bank of elevators that serve the same sets of floors, both above 
  and below "ground" (floor 0).
- An Elevator that implements a basic LOOK algorithm:  it'll serve all requests in
  one direction, before changing direction to serve requests in the other direction.
- An elevator that will serve priority requests first (for example: fire, security, etc).
- An eventually consistent de-centralized coordination mechanism (gossip protocol) for 
  Elevators in a bank to bid on requests.

## Design

### Elevator Bank

This is an oversimplified set of elevators serving the same set of floors that puts out
for bid service requests.  In practice this bank of elevators would likely be managed
within the deployment topology and requests would be consumed from a message queue.

Not implemented:

- Banks where some elevators serve specific floors
- Network partitioning, jitter, retry, etc
- Internet requests (e.g. security from mobile phone requests)

### Elevator

Represents a basic Elevator design that can bid on requests, gossip with known
peers to propagate state, and implements a basic LOOK algoritm.

This elevator is intended to be extended.  As opposed to being composed, much
of the needed state (queues for work) makes up a significant part of this class.
This could be revisited however - the gossip mechanism could, and likely should
be factored out, so that more advanced elevators (e.g. express or service),
could be more easily built.

Events such as a fire, or security, are intended to be construed through higher
priority floor requests.

**Efficiency**:  The built-in LOOK algorithm acts like most elevator rides, however
it's likely not ideal in all circumstances.  Rather it seems that the most effecient
approach could vary widely:  holidays, emergencies, construction, and more
could all require more dynamically adaptive controls in reality.

Not implemented:

- Express (skip floors), service
- Security - elevators that go to a floor only by authorized request.
  This could be implemented easily with a strategy design pattern in
  an Elevator's addRequest().
- Default or set positions:  through schedules or security incidents
- Physical control pieces:
  - emergency brakes
  - inertia, speed, etc
  - Doors
  - People and crowding (SimTower already did it best)
  - Earthquakes / tornadoes / Godzilla / Xenomorphs

### Coordination

In considering how a bank of elevators would coordinate both centralized
and decentralized approaches were considered.  In reality it may very
well be that elevator banks are centrally coordinated - this would be
simple and effective, and the risk of a single point of failure might
be acceptable in real use-cases.

However, reality here doesn't apply, and exploring basic decentralized
approaches is a good exercise.  Two main approaches were considered:

- Raft, with it's linerizability characteristic, is strongly consistent,
  however in an elevator bank this seems not terribly important:  if
  2 or more elevators end up serving a request in a network partition
  (not simulated here), then that's OK.  In fact it could be advantageous
  for more elevators to serve a request than strictly required, as our
  sensors for knowing what the request is are essentially floor and direction
  (and sometimes priority).  We don't know necessarily how many people are
  waiting, nor how many people are in the elevator.
- Gossip, with its eventual consistency gurantee is both simple, and
  likely more relevant: it might over-serve a request in a network
  partition, but it will serve it quicker.  Being more available
  is likely beneficial for a bank of elevators, especially those
  run by frantic Mr. Basil in his Fawlty Towers.