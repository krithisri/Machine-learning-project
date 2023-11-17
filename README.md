# Exception Handling Practices and Post-release Defects Study
This is part of the project for course: SOEN 6591 in Data Mining and Analysis

## Overview

Welcome to the repository for the replication study titled "Studying the Relationship between Exception Handling Practices and Post-release Defects." This research, conducted by Srikrithi Chamarthi, Anusha Yeramalla, and Bala Sharanya Devarapu from the Dept. of Engineering and Computer Science at Concordia University, Montreal, Quebec, Canada, delves into the intricate connection between exception handling practices and post-release defects in software development.

## Abstract

Understanding how exception handling practices influence software quality is critical in preventing post-release defects. This study, a replication of prior work, focuses on the Apache Kafka project, specifically version 3.2.0. By examining pre-release defects, post-release defects, exception flow characteristics, and exception anti-pattern metrics, the researchers aim to construct models that provide insights into effective exception handling.

## Key Findings

- **Exception Flow Characteristics Significance:** The study reveals that exception flow characteristics in Java projects play a significant role in explaining post-release defects, complementing traditional software metrics.
  
- **Impact of Exception Handling Anti-patterns:** While some exception handling anti-patterns show a positive relationship with the probability of post-release defects, it is cautioned that they are not the sole indicators. Developers are encouraged to be aware of these anti-patterns but should consider them as part of a holistic analysis of software quality.

## Subject Projects

The primary subject of this study is the Apache Kafka project, developed in Java. Version 3.2.0 is chosen for stability and data availability, and various metrics are collected, including post-release defects, traditional product metrics, traditional process metrics, and exception handling metrics.

## Metrics

### Post-release Defects

- **Collection Method:** Obtained from Jira, considering resolved and closed defects after the version release.

### Traditional Product Metrics

- **Calculation Method:** Calculated using a static code analysis tool called Understand, including size and Cyclomatic complexity.

### Traditional Process Metrics

- **Change Metrics:** Based on pre-release changes, considering all changes made before the release.
  
- **Pre-release Quality Metrics:** Extracted from Jira, combining issue IDs with git log files to extract file names and the number of pre-release bugs.

### Exception Handling Metrics

- **Exception Flow Characteristics:** Metrics describing the characteristics of exception flow, obtained from the Kafka repository using ASTParser.

- **Exception Handling Anti-patterns:** Focus on four types of anti-patterns, including Throws Kitchen Sink, Log and Throw, Nested Try, and Destructive Wrapping.

## Model Construction

The study constructs three types of models:

1. **BASE Model:** Includes all product metrics and process metrics from the Understand tool.
  
2. **BSFC Model (Base + Exception Flow Characteristics):** Merged with Flow metrics data, emphasizing the impact of exception flow characteristics on post-release defects.
  
3. **BSAP Model (Base + Anti-patterns):** Merged with Anti-pattern data, aiming to understand the impact of exception handling anti-patterns on post-release defects.

### Model Building Techniques

- **Algorithm:** Logistic Regression is employed to build the models. This statistical method allows us to model the probability of post-release defects as a function of various metrics.

- **Model Stability Assessment:** The Nagelkerke-R2 statistic is utilized to evaluate the accuracy of the models. Bootstrap is performed to ensure model stability, and an optimism-reduced Nagelkerke-R2 is calculated to account for predictor noise.

- **Model Simplification:** Insignificant predictors are iteratively removed from the models using fast backward predictor selection until only significant predictors remain, with a significance level of 0.05.

## Results and Conclusion

The results of this study indicate that exception flow characteristics significantly contribute to explaining post-release defects. While some exception handling anti-patterns show a positive relationship with the probability of post-release defects, they should not be solely relied upon as predictors. The study underscores the need for proper exception handling practices and provides insights that can inform software development practices.


## Replicate the Study

If you wish to replicate or extend this study, detailed instructions and explanations are provided in the paper and code folder. Feel free to explore, analyze, and contribute to advancing our understanding of exception handling practices and their impact on software quality.

## References

1. M. Wiang, ”Studying the Relationship between Exception Handling Practices and Postrelease Defects,” in IEEE Transactions on Software Engineering, vol. 45, no. 10, pp. 993-1014, Oct. 2019, doi: 10.1109/TSE.2018.2872144.
2. S. Padua and W. Shang,”Revisiting Exception Handling Practices with Exception Flow Analysis,” in Proceedings of the 27th ACM SIGSOFT International Symposium on Software Testing and Analysis, New York, NY, USA, Jul. 2018, pp. 84- 94, doi: 10.1145/3213846.3213863
