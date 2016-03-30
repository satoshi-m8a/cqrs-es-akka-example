import {DiscussionsService} from '../service/discussions.service';
import {createStore,combineReducers} from 'redux';
import {reducerDiscussion,reducerDiscussions} from './discussions.reducer';
import {Discussion} from '../model/discussion';
import {loadDiscussion} from './discussions.action';
import {Injectable} from 'angular2/core';
import {loadComments} from './discussions.action';
import {reducerComments} from './discussions.reducer';

@Injectable()
export class DiscussionsStore {
    store:Redux.Store;

    constructor(private discussionsService:DiscussionsService) {
        this.store = createStore(combineReducers({discussion:reducerDiscussion, discussions:reducerDiscussions, comment:reducerComments}), {});
    }

    get discussion() {
        return this.store.getState().discussion;
    }

    get comments() {
        return this.store.getState().comment;
    }

    getDiscussion(id:string) {
        this.discussionsService.getDiscussion(id).subscribe((discussion:Discussion)=> {
            this.store.dispatch(loadDiscussion(discussion));
        });
    }

    getComments(id:string) {
        this.discussionsService.getComments(id).subscribe((comments:Array<Comment>)=> {
            this.store.dispatch(loadComments(comments));
        });
    }

}
