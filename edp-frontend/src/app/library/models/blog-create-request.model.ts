export interface BlogCreateRequest {
  title: string;
  description: string;
  documentUrl: string;
  tagIds: number[];
}
