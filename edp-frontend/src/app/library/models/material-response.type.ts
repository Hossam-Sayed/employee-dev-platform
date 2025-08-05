import { BlogResponse } from './blog-response.model';
import { LearningResponse } from './learning-response.model';
import { WikiResponse } from './wiki-response.model';

export type MaterialResponse = LearningResponse | BlogResponse | WikiResponse;
