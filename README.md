# Consistent Hashing Simulator

A Spring Boot application that lets you experiment with consistent hashing. This interactive simulator helps you understand how consistent hashing works and visualize its behavior in distributed systems.

## What is Consistent Hashing?

Consistent hashing is a distributed hashing technique that provides a way to distribute data across multiple servers while minimizing reorganization when servers are added or removed.

### The Problem It Solves

In traditional hash-based data distribution:
- Data is distributed across n servers by hashing the key and using modulo n
- When a server is added or removed, almost all keys need to be remapped
- This causes massive data migration, which is expensive and disruptive

Consistent hashing solves this by:
- Mapping both servers and keys to the same hash space (typically represented as a ring)
- When a server is added or removed, only a small fraction of keys need to be remapped
- This minimizes data migration and service disruption

### How It Works

1. Both servers (nodes) and data keys are hashed to positions on a conceptual circle or ring
2. Each key is assigned to the first server encountered when moving clockwise from the key's position
3. When a server is added, it only takes keys from the next server clockwise on the ring
4. When a server is removed, its keys are reassigned to the next server clockwise

### Virtual Nodes

To improve distribution, this implementation uses virtual nodes:
- Each physical server is represented by multiple virtual nodes on the ring
- This helps achieve a more balanced distribution of keys
- Each node defaults to 5 virtual nodes (configurable via the `app.virtual-nodes` property in `application.properties`).

### Benefits

- Minimizes data movement when scaling up or down
- Provides natural load balancing
- Enables horizontal scaling of distributed systems
- Critical for distributed databases, caches, and content delivery networks

## Build & Run

```bash
mvn spring-boot:run
```

## API Reference

### Nodes API

* `POST /nodes?id=nodeA` – Add a node to the hash ring
  - Request parameter: `id` - Unique identifier for the node
  - Response: JSON with confirmation message and node ID
  - Example: `curl -X POST "http://localhost:8080/nodes?id=nodeA"`

* `DELETE /nodes/{id}` – Remove a node from the hash ring
  - Path variable: `id` - Identifier of the node to remove
  - Response: JSON with confirmation message and node ID
  - Example: `curl -X DELETE "http://localhost:8080/nodes/nodeA"`

* `GET /nodes` – List all nodes in the system
  - Response: JSON array of node objects with their IDs and assigned keys
  - Example: `curl "http://localhost:8080/nodes"`

* `GET /nodes/ring` – View the hash ring mapping
  - Response: JSON map of hash values to node IDs
  - Example: `curl "http://localhost:8080/nodes/ring"`

### Data API

* `POST /data?key=someKey` – Route a key and assign it to the appropriate node
  - Request parameter: `key` - The data key to route
  - Response: JSON with the key and the ID of the node it was assigned to
  - Example: `curl -X POST "http://localhost:8080/data?key=user123"`

* `GET /data/{key}` – Lookup which node owns a specific key
  - Path variable: `key` - The data key to locate
  - Response: JSON with the key and the ID of the node it's assigned to
  - Example: `curl "http://localhost:8080/data/user123"`

## Web Interface

The application includes a web interface that visualizes the consistent hash ring and allows you to:
- Add and remove nodes
- Add data keys
- See which node each key is assigned to
- Visualize the distribution of keys and nodes on the hash ring

Access the web interface by opening a browser to `http://localhost:8080` after starting the application.
