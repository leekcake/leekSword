package moe.leekcake.sword

import java.lang.ref.WeakReference
import java.util.*

/**
 * WeakReference 로 Wrap 한 자료 저장소
 */
class WeakStore<Data> {
    val datas: MutableList<WeakReference<Data>>;

    operator fun iterator(): MutableIterator<WeakReference<Data>> {
        return datas.iterator();
    }

    constructor(threadSafe: Boolean = false) {
        if(threadSafe) {
            datas = (Collections.synchronizedList( ArrayList<WeakReference<Data>>() )!!) as MutableList<WeakReference<Data>>;
        } else {
            datas = ArrayList();
        }
    }

    fun add(data: Data) {
        datas.add( WeakReference(data) );
    }

    fun addFirst(data: Data) {
        datas.add(0, WeakReference(data) );
    }

    fun remove(data: Data) {
        removeFor(data);
    }

    fun valid() {
        removeFor(null); //null 이라면 멈추지 않기 때문에, 전체 검사
    }

    /**
     * 리스트를 돌며 항목을 찾아 삭제합니다.
     * 만약 항목이 아니지만 이미 사라진 데이터가 있다면 리스트에서 제거합니다
     */
    private fun removeFor(user: Data?) {
        val iter = datas.iterator();

        while( iter.hasNext() ) {
            val refer = iter.next();
            if(refer.get() === null) {
                iter.remove();
            } else if(refer.get() === user) {
                iter.remove();
                break;
            }
        }
    }
}