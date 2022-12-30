package strInfo.result;

/**
 * 所有语句对象的建造者类，该类是所有Builder类的抽象。
 * <p>
 * The builder class of all statement objects, which is the abstract of all Builder classes.
 *
 * @param <Product> 该类所构造出来的对象类型，由该参数指定该Builder的返回数值。
 */
public interface Builder<Product> {

    /**
     * @return 建造者类所构造出来的数据封装对象，当调用该方法的时候意味着建造完成，将会正式的确定出被构造产品的结果数据
     */
    Product create();
}
