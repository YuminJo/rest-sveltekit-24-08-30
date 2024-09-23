/**
 * This file was auto-generated by openapi-typescript.
 * Do not make direct changes to the file.
 */

export interface paths {
    "/api/v1/posts": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: operations["getPosts"];
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
    "/api/v1/posts/{id}": {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        get: operations["getPost"];
        put?: never;
        post?: never;
        delete?: never;
        options?: never;
        head?: never;
        patch?: never;
        trace?: never;
    };
}
export type webhooks = Record<string, never>;
export interface components {
    schemas: {
        GetPostsResponseBody: {
            items: components["schemas"]["PostDto"][];
        };
        PostDto: {
            /** Format: int64 */
            id: number;
            /** Format: date-time */
            createDate: string;
            /** Format: date-time */
            modifyDate: string;
            /** Format: int64 */
            authorId: number;
            authorName: string;
            title: string;
            body: string;
        };
        RsDataGetPostsResponseBody: {
            resultCode: string;
            /** Format: int32 */
            statusCode: number;
            msg: string;
            data: components["schemas"]["GetPostsResponseBody"];
            fail: boolean;
            success: boolean;
        };
        GetPostResponseBody: {
            item: components["schemas"]["PostDto"];
        };
        RsDataGetPostResponseBody: {
            resultCode: string;
            /** Format: int32 */
            statusCode: number;
            msg: string;
            data: components["schemas"]["GetPostResponseBody"];
            fail: boolean;
            success: boolean;
        };
    };
    responses: never;
    parameters: never;
    requestBodies: never;
    headers: never;
    pathItems: never;
}
export type $defs = Record<string, never>;
export interface operations {
    getPosts: {
        parameters: {
            query?: never;
            header?: never;
            path?: never;
            cookie?: never;
        };
        requestBody?: never;
        responses: {
            /** @description OK */
            200: {
                headers: {
                    [name: string]: unknown;
                };
                content: {
                    "*/*": components["schemas"]["RsDataGetPostsResponseBody"];
                };
            };
        };
    };
    getPost: {
        parameters: {
            query?: never;
            header?: never;
            path: {
                id: number;
            };
            cookie?: never;
        };
        requestBody?: never;
        responses: {
            /** @description OK */
            200: {
                headers: {
                    [name: string]: unknown;
                };
                content: {
                    "*/*": components["schemas"]["RsDataGetPostResponseBody"];
                };
            };
        };
    };
}
