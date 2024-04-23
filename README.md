# GeneBankSearchBTree

GeneBankSearchBTree is a Java program that searches DNA sequences in a B-tree index and computes their frequencies. It is part of a larger project that includes creating and searching a GeneBank database.

## Team Name

Team 1

## Team Members

| Last Name | First Name | GitHub User Name    |
|-----------|------------|---------------------|
| Kratzer   | Bryce      | brycekratzer        |
| Reynolds  | Gabriel    | GABRIELREYNOLDS-alt |
| Chaney    | Sean       | seanbchaney         |

## Project Overview

The GeneBankSearchBTree program takes command-line arguments to specify the B-tree file, query file, and other parameters. It then searches for DNA sequences in the B-tree index and computes their frequencies. The program utilizes a custom B-tree implementation and supports features like caching for improved performance.

## Implementation Details

The main components of the GeneBankSearchBTree program are:

1. `GeneBankSearchBTree`: The main class that handles the program flow, including parsing arguments, searching keys, and outputting results.
2. `GeneBankSearchBTreeArguments`: A class that represents the command-line arguments for the program.
3. `BTree`: A custom B-tree implementation that provides methods for searching, inserting, and traversing the B-tree.
4. `TreeObject`: A class that represents an object stored in the B-tree, containing a value and its frequency.
5. `Cache`: A class that implements a cache storage mechanism for B-tree nodes using a least recently used (LRU) eviction policy.

The program follows these steps:

1. Parse the command-line arguments and handle any exceptions.
2. Create a B-tree instance using the specified degree and B-tree file.
3. Search for keys from the query file and compute their frequencies.
4. Write the search results to an output file or increment the sequence count based on the debug level.
5. Output debug information, including elapsed time, number of DNA sequences scanned, and B-tree statistics.

The B-tree implementation supports disk-based storage, allowing the program to handle large datasets efficiently. The cache implementation helps improve performance by reducing disk I/O operations.

## Usage

To run the GeneBankSearchBTree program, use the following command:

```
java GeneBankSearchBTree --cache=<0|1> --degree=<btree-degree> --btreefile=<b-tree-file> --length=<sequence-length> --queryfile=<query-file> [--cachesize=<n>] [--debug=0|1]
```

- `--cache`: Specifies whether to use caching (1) or not (0).
- `--degree`: The degree of the B-tree.
- `--btreefile`: The name of the B-tree file.
- `--length`: The length of the DNA subsequence.
- `--queryfile`: The name of the query file containing DNA sequences to search for.
- `--cachesize`: The size of the cache (optional, required if `--cache=1`).
- `--debug`: The debug level (0 for no debug output, 1 for debug output).

## Test Results

All dumpfiles and query file results matched using the provided check-dumpfiles.sh and check-queries.sh scripts.

## Cache Performance Results

| gbk file | degree | sequence length | cache | cache size | cache hit rate | run time |
|----------|--------|-----------------|-------|------------|----------------|----------|
| test5.gbk | 85     | 20              | no    | 0          | 0%             | 23.78s   |
| test5.gbk | 85     | 20              | yes   | 100        | 8.82%          | 21.47s   |
| test5.gbk | 85     | 20              | yes   | 500        | 31.55%         | 15.75s   |
| test5.gbk | 85     | 20              | yes   | 1000       | 44.22%         | 13.62s   |
| test5.gbk | 85     | 20              | yes   | 5000       | 68.32%         | 11.10s   |
| test5.gbk | 85     | 20              | yes   | 10000      | 80.92%         | 9.08s    |

The cache performance results demonstrate the impact of caching on the program's runtime. As the cache size increases, the cache hit rate improves, resulting in faster execution times.

## AWS Notes

The team encountered some challenges while working with AWS, primarily due to heap size errors and out-of-memory issues. After increasing the heap size to 3GB, the issues were resolved. It required some effort to get the program running smoothly on AWS.

## Reflections

### Bryce Kratzer

This project was one of the most challenging assignments I have undertaken so far in my academic journey. Despite the difficulties, it was an incredibly enjoyable and rewarding experience in its own unique way.

I am truly grateful to have been paired with such an outstanding team that showed productivity and a strong team-oriented mindset. Their amazing support and collaboration were instrumental in achieving the progress we made. It would have been impossible to reach this point without the dedication and effort of each team member.

Pinpointing the single most challenging aspect of this assignment is, ironically, a challenging task in itself. The most difficult components included the implementation of the BTree data structure, ensuring the accuracy of frequency counts, and grasping an understanding of cache implementation. However, the sense of accomplishment that came with overcoming each obstacle and completing each task, after investing significant time and effort, was undoubtedly my favorite part of this assignment.

Overall, while this project posed substantial challenges, it also yielded immense rewards. The synergy of our team's collective effort, combined with my individual dedication, enabled us to successfully complete this project within the given timeframe.

### Sean Chaney

Working on this project was both challenging and rewarding. It provided a great opportunity to apply the concepts learned throughout the course to a real-world bioinformatics problem. Here are some reflections on my experience:

- The project required a solid understanding of data structures, file handling, and database management. I had to spend some time revisiting these concepts and understanding how they could be applied to the specific problem of handling DNA sequences.

- One of the major challenges was designing and implementing the B-Tree data structure efficiently. Optimizing memory usage and disk I/O operations while ensuring correctness was a delicate balance. Additionally, handling edge cases and error conditions required thorough testing and debugging.

- Implementing features like caching and leveraging SQLite for database storage was crucial for improving the performance of the program, especially when dealing with large datasets. Experimenting with different cache sizes and B-Tree degrees helped in finding the optimal configurations.

- Working in a team allowed us to leverage each other's strengths and divide the workload effectively. Regular meetings and communication ensured that everyone was on the same page and progress was made consistently.

- Managing time effectively was crucial, especially with deadlines approaching. Breaking down the project into smaller tasks and setting realistic timelines helped in staying organized and meeting deliverables on time.

Overall, this project provided a valuable hands-on experience in software development for bioinformatics applications. It reinforced the importance of applying theoretical knowledge to practical problems and highlighted the significance of collaboration, testing, and continuous improvement in the software development process.

## Additional Notes

### Evidence of Group Team Work Through our Group Chat

The team utilized a group chat for communication and collaboration throughout the project. Screenshots of the group chat are provided in the 'untitled photo' to demonstrate the team's active engagement and coordination.
